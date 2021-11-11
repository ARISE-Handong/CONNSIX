use std::io::prelude::* ;
use std::net::TcpListener ;
use std::thread ;
use std::time::Duration ;
use std::sync::mpsc ;
use std::sync::{Arc, Mutex, Condvar} ;
use std::io ;

fn main() {
	let pair = Arc::new((Mutex::new(2u32),  Condvar::new())) ; // conditional variable
	let pair2 = Arc::clone(&pair) ;
	let pair3 = Arc::clone(&pair) ;
	
    // [player - main] channel
	let (black_tx_agent, black_rx_main) = mpsc::channel() ;
	let (black_tx_main, black_rx_agent) = mpsc::channel() ;
	let black_player = thread::spawn(move ||{
		agent_thread(8081, black_tx_agent, black_rx_agent, pair2) ;
	});
	let (white_tx_agent, white_rx_main) = mpsc::channel() ;
	let (white_tx_main, white_rx_agent) = mpsc::channel() ;
	let white_player = thread::spawn(move ||{
		agent_thread(8082, white_tx_agent, white_rx_agent, pair3) ;
	});

    // wait for player connection
	let (lock, cvar) = &*pair ;
	let mut waiting = lock.lock().unwrap() ;
	while *waiting != 0 {
		waiting = cvar.wait(waiting).unwrap() ;
	}

    // start match
	println!("Press any key to start the match...") ;
	let mut c:[u8; 1] = [0; 1];
	io::stdin().read(&mut c) ;
	let redstones = String::from("A01") ;
	black_tx_main.send(redstones.clone()).unwrap() ;
	white_tx_main.send(redstones.clone()).unwrap() ;
	loop {
		let from_black = black_rx_main.recv().unwrap() ;
		println!("from_black: {}", &from_black) ;
		white_tx_main.send(from_black).unwrap() ;
		thread::sleep(Duration::from_secs(1)) ;
		let from_white = white_rx_main.recv().unwrap() ;
		println!("from_white: {}", &from_white) ;
		black_tx_main.send(from_white).unwrap() ;
	}
}
fn agent_thread (port: u16, main_tx: mpsc::Sender<String>, main_rx: mpsc::Receiver<String>, pair: Arc<(Mutex<u32>, Condvar)>) {
	
	let addr = format!("127.0.0.1:{}", port) ;
	let listener = TcpListener::bind(addr).unwrap() ;
	if let Ok((mut socket, addr)) = listener.accept() {
        // wait for conditional variable
		{
			let (lock, cvar) = &*pair ;
			let mut waiting = lock.lock().unwrap() ;
			*waiting -= 1 ;
			cvar.notify_one() ;
		}

        // if white
		if port == 8082 {
            // get red stones
			let from_main = main_rx.recv().unwrap() ;
			let from_main_size = from_main.len() as u32 ;
			println!("{} from_main: {}", port, &from_main) ;
            // send to player
			socket.write(&from_main_size.to_ne_bytes()) ;
			socket.write(from_main.as_bytes()).unwrap() ;
		}
		loop {
            // get stones from main
			let from_main = main_rx.recv().unwrap() ;
			let from_main_size = from_main.len() as u32 ;
			println!("{} from_main: {}", port, &from_main) ;

            // send to player (tcp)
			socket.write(&from_main_size.to_ne_bytes()) ;
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