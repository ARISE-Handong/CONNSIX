/**
 * GUI 연결
 * GUI에서 게임 세팅 메세지 받기
 * player 연결
 * GUI에 연결 성공 메세지 보내기
 * GUI에서 게임 시작 메세지 받기
 * 1. 흑백돌에 적돌 정보 보내기
 * 2. 흑돌에게서 첫수 (k10) 정보 받기
 * -- 반복 --
 * 1) GUI에 수 정보 메세지 전달
 * 2) 일정 시간 후 상대방에 수 정보 메세지 전달 및 카운팅 시작
 * 
 */

use std::thread;
use std::io::prelude::*;
use std::time::Duration;

use server::UserInterface;

fn main() {
    // GUI 연결
    let gui = UserInterface::new(String::from("172.20.10.2"), 8083);
    
    // setting 받기
    let red_stones = gui.pull();
    println!("main red: {}", red_stones);
    let black_port = gui.pull();
    println!("main black port: {}", black_port);
    let white_port = gui.pull();
    println!("main white port: {}", white_port);
    let interval = gui.pull();
    println!("main interval: {}", interval);

    // setting 정보 정리
    
    // Player 연결
    let ip = String::from("127.0.0.1");
    let (black_player, white_player) = server::connect_player(ip, 8081, 8082);

    // GUI에 성공 메세지 보내기
    gui.push(String::from("READY"));

    // GUI에서 시작 정보 받기

    println!("gui hi from main?");
    let start = gui.pull();
    println!("start: {}\n", start);

    // 적돌 정보 보내기
    // let red_stones = String::from("A01");
    black_player.push(red_stones.clone());
    white_player.push(red_stones.clone());

    // 무한 반복
        // 돌 받기
        // check valid
        // 돌 보내기
    loop {
        let from_black = black_player.pull();
		println!("main from_black: {}", &from_black);

		gui.push(format!("B{}", from_black));
		white_player.push(from_black.clone());
		
        let from_white = white_player.pull();
		println!("main from_white: {}", &from_white) ;
		
        gui.push(format!("W{}", from_white.clone()));
        black_player.push(from_white);

		thread::sleep(Duration::from_secs(1)) ;
    }
    
}
