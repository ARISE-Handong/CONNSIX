use std::thread;
use std::net::TcpListener;
use std::sync::{Arc, Mutex, Condvar};
use std::sync::mpsc;
use std::io::prelude::*;

pub struct Player {
    thread: thread::JoinHandle<()>,
    tx: mpsc::Sender<String>, // sends message from main to thread 
    rx: mpsc::Receiver<String>, // receives message from thread to main
}

impl Player {
    pub fn new(
        color: Color,
        port: u16,
        pair: Arc<(Mutex<u32>, Condvar)>
    )
    -> Player
    {
        let (tx_agent, rx_main) = mpsc::channel();
        let (tx_main, rx_agent) = mpsc::channel();
        println!("[new] color: {:?}", color);
        let thread = spawn(color, port, tx_agent, rx_agent, pair);

        Player {
            thread: thread,
            tx: tx_main,
            rx: rx_main,
        }
    }

    pub fn push(&self, push_stone: String) {
        self.tx.send(push_stone).unwrap();
    }
    
    pub fn pull(&self) -> String {
        // channel로 thread에서 받은 결과 리턴 
        self.rx.recv().unwrap()
    }
}

#[derive(Debug)]
pub enum Color {
    Black,
    White,
}

fn spawn(color: Color, port: u16, main_tx: mpsc::Sender<String>, main_rx: mpsc::Receiver<String>, pair: Arc<(Mutex<u32>, Condvar)>) -> thread::JoinHandle<()> {
    thread::spawn(move || {
        let addr = format!("127.0.0.1:{}", port) ;
        let listener = TcpListener::bind(addr).unwrap() ;
        if let Ok((mut socket, _addr)) = listener.accept() {
            // wait for conditional variable
            {
                let (lock, cvar) = &*pair ;
                let mut waiting = lock.lock().unwrap() ;
                *waiting -= 1 ;
                cvar.notify_one() ;
            }
    
            match color {
                Color::White  => {
                    // get red stones
                    let from_main = main_rx.recv().unwrap() ;
                    let from_main_size = from_main.len() as u32 ;
                    println!("white from_main: {}", &from_main) ;
                    // send to player
                    socket.write(&from_main_size.to_ne_bytes()).unwrap() ;
                    socket.write(from_main.as_bytes()).unwrap() ;
                },
                _ => (),
            }

            loop {
                // get stones from main
                let from_main = main_rx.recv().unwrap() ;
                let from_main_size = from_main.len() as u32 ;
                println!("{} from_main: {}", port, &from_main) ;
    
                // send to player (tcp)
                socket.write(&from_main_size.to_ne_bytes()).unwrap() ;
                socket.write(from_main.as_bytes()).unwrap() ;
    
                // get byte (size of message) from player (tcp)
                let mut header = [0u8; 4] ;
                socket.read_exact(&mut header) ;
                let size = u32::from_ne_bytes(header) ; // network endian 바꾸기
    
                // read stone data from player (tcp)
                let mut data = vec![0; size as usize];
                socket.read_exact(&mut data) ;
    
                // send main the stone data in String type
                let to_main = String::from_utf8_lossy(&data) ;
                main_tx.send(to_main.to_string()).unwrap() ;
            }
        }
    })
}

pub fn connect_player(black_port: u16, white_port: u16) -> (Player, Player) {
    let pair = Arc::new((Mutex::new(2u32),  Condvar::new())) ; // conditional variable
	let pair2 = Arc::clone(&pair) ;
	let pair3 = Arc::clone(&pair) ;

    let black = Player::new(Color::Black, black_port, pair2);
    let white = Player::new(Color::White, white_port, pair3);

    // wait for player connection
	let (lock, cvar) = &*pair ;
	let mut waiting = lock.lock().unwrap() ;
	while *waiting != 0 {
		waiting = cvar.wait(waiting).unwrap() ;
	}

    (black, white)
}

pub fn print_type_of<T>(_: &T) {
    println!("{}", std::any::type_name::<T>())
}