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

fn main() {
    // GUI 연결
    // setting 받기
    // Player 연결
    let (black_player, white_player) = server::connect_player(8081, 8082);

    // GUI에 성공 메세지 보내기
    // GUI에서 시작 정보 받기
    println!("Press any key to start the match...") ;
	let mut c:[u8; 1] = [0; 1];
    std::io::stdin().read(&mut c);

    // 적돌 정보 보내기
    let red_stones = String::from("A01");
    black_player.push(red_stones.clone());
    white_player.push(red_stones.clone());

    // 무한 반복
        // 돌 받기
        // check valid
        // 돌 보내기
    loop {
        let from_black = black_player.pull();
		println!("from_black: {}", &from_black) ;
		white_player.push(from_black);
		thread::sleep(Duration::from_secs(1)) ;
		let from_white = white_player.pull();
		println!("from_white: {}", &from_white) ;
		black_player.push(from_white);
    }
    
}
