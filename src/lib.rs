#![allow(unused)]
use std::thread;
use std::net::TcpListener;
use std::sync::{Arc, Mutex, Condvar};
use std::sync::mpsc;
use std::io::prelude::*;

mod board_operation;
use board_operation::*;

pub struct UserInterface {
    thread: thread::JoinHandle<()>,
    tx: mpsc::Sender<String>,
    rx: mpsc::Receiver<String>,
}

impl UserInterface {
    pub fn new(ip: String, port: u16) -> UserInterface {

        let (tx_agent, rx_main) = mpsc::channel();
        let (tx_main, rx_agent) = mpsc::channel();
        let thread = thread::spawn(move || {
            gui_thread(ip, port, tx_agent, rx_agent);
        });

        UserInterface {
            thread,
            tx: tx_main,
            rx: rx_main,
        }
    }

    pub fn push(&self, message: String) {
        self.tx.send(message).unwrap();
    }

    pub fn pull(&self) -> String {
        self.rx.recv().unwrap()
    }

    pub fn get_settings(&self) -> (String, u16, u16, u16){
        let red_stones = self.pull();
        let black_port = (self.pull()).parse::<u16>().unwrap();
        let white_port = (self.pull()).parse::<u16>().unwrap();
        let interval = (self.pull()).parse::<u16>().unwrap();
        (red_stones, black_port, white_port, interval)
    }
}

fn gui_thread(ip: String, port: u16, tx: mpsc::Sender<String>, rx: mpsc::Receiver<String>) {
    let addr = format!("{}:{}", ip, port);
    println!("addr {}", addr);
    let listener = TcpListener::bind(addr).unwrap();
    if let Ok((mut socket, _addr)) = listener.accept() {
        println!("GUI connected!");

        // setting - red stones 받기
        let mut header = [0u8; 4]; // message size
        socket.read_exact(&mut header);
        let size = u32::from_ne_bytes(header);

        let mut data = vec![0; size as usize]; // data
        socket.read_exact(&mut data);

        let to_main = String::from_utf8_lossy(&data);
        tx.send(to_main.to_string()).unwrap(); // sending to main

        // setting - black port 받기
        let mut data = [0u8; 4]; // data
        socket.read_exact(&mut data);
        let black_port = u32::from_ne_bytes(data);

        let to_main = black_port.to_string();
        tx.send(to_main.to_string()).unwrap(); // sending to main

        // setting - white port 받기
        let mut data = [0u8; 4]; // data
        socket.read_exact(&mut data);
        let white_port = u32::from_ne_bytes(data);

        let to_main = white_port.to_string();
        tx.send(to_main.to_string()).unwrap(); // sending to main

        // setting - interval 받기
        let mut data = [0u8; 4]; // data
        socket.read_exact(&mut data);
        let interval = u32::from_ne_bytes(data);

        let to_main = interval.to_string();
        tx.send(to_main.to_string()).unwrap(); // sending to main

        // 성공 메세지 보내기
        let from_main = rx.recv().unwrap();
        let from_main_size = from_main.len() as u32;

        socket.write(&from_main_size.to_ne_bytes());
        socket.write(from_main.as_bytes()).unwrap();

        // start 메세지 받기
        let mut header = [0u8; 4];
        socket.read_exact(&mut header);
        let size = u32::from_ne_bytes(header);

        let mut data = vec![0; size as usize];
        socket.read_exact(&mut data);

        // println!("gui setting: {}", String::from_utf8_lossy(&data));

        let to_main = String::from_utf8_lossy(&data);
        tx.send(to_main.to_string()).unwrap();

        // GUI
        loop {
            let from_main = rx.recv().unwrap();
            let from_main_size = from_main.len() as u32;

            // println!("gui from main {}", from_main);

            socket.write(&from_main_size.to_ne_bytes());
            socket.write(from_main.as_bytes()).unwrap();
        }
    }
    else {
        println!("Not connected!");
    }
}

pub struct Player {
    thread: thread::JoinHandle<()>,
    tx: mpsc::Sender<String>, // sends message from main to thread 
    rx: mpsc::Receiver<String>, // receives message from thread to main
}

impl Player {
    pub fn new(
        color: Color,
        ip: String,
        port: u16,
        pair: Arc<(Mutex<u32>, Condvar)>
    )
    -> Player
    {
        let (tx_agent, rx_main) = mpsc::channel();
        let (tx_main, rx_agent) = mpsc::channel();
        let thread = thread::spawn(move || {
            player_thread(color, ip, port, tx_agent, rx_agent, pair);
        });

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

pub enum Color {
    Black,
    White,
}

fn player_thread(color: Color, ip: String, port: u16, main_tx: mpsc::Sender<String>, main_rx: mpsc::Receiver<String>, pair: Arc<(Mutex<u32>, Condvar)>){
    let addr = format!("{}:{}", ip, port) ;
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
}

pub fn connect_player(ip: String, black_port: u16, white_port: u16) -> (Player, Player) {
    let pair = Arc::new((Mutex::new(2u32),  Condvar::new())) ; // conditional variable
	let pair2 = Arc::clone(&pair) ;
	let pair3 = Arc::clone(&pair) ;

    let black = Player::new(Color::Black, ip.clone(), black_port, pair2);
    let white = Player::new(Color::White, ip, white_port, pair3);

    // wait for player connection
	let (lock, cvar) = &*pair ;
	let mut waiting = lock.lock().unwrap() ;
	while *waiting != 0 {
		waiting = cvar.wait(waiting).unwrap() ;
	}

    (black, white)
}

#[cfg(test)]
mod tests {
    use super::*;
    // use server::UserInterface;
    
    #[test]
    fn printing_board() {
        println!("printing_board");
        let mut board = Board::new("F03:C03:K11");
        board.check_and_forward("K10", BLACK);
        board.check_and_forward("A09:J03", WHITE);
        board.print_board();
        assert!(false);
    }
}