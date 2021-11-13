#![allow(unused)]
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
use std::time::{Duration, Instant};
mod board_operation;
use board_operation::*;
use server::UserInterface;
use server::Color;

fn main() {
    // GUI 연결
    // let ip = String::from("172.20.10.2");
    let ip = String::from("127.0.0.1");
    // let ip = String::from("192.168.53.102");
    let port = 8089;
    let gui = UserInterface::new(ip, port);
    
    // setting 받기
    let (red_stones, black_port, white_port, interval) = gui.get_settings();
    println!("main red: {}", red_stones);
    println!("main black port: {}", black_port);
    println!("main white port: {}", white_port);
    println!("main interval: {}", interval);

    // board 생성
    let mut  board = Board::new(&red_stones);
    board.print_board();

    // Player 연결
    let ip = String::from("127.0.0.1");
    let (black_player, white_player) = server::connect_player(ip, black_port, white_port);

    // GUI에 성공 메세지 보내기
    gui.push("READY".to_string());

    // GUI에서 시작 정보 받기
    let start = gui.pull();
    println!("start: {}\n", start);

    // send red stones
    black_player.push(red_stones.clone());
    white_player.push(red_stones);

    let interval = interval * 1000;
    loop {
        // pull black
        let now = Instant::now();
        let from_black = black_player.pull();
        let elapse = now.elapsed().as_millis();
        if elapse < interval {
            thread::sleep(Duration::from_millis((interval - elapse) as u64));
        }
		println!("Black: {}", &from_black);

        // add board & push gui
        let valid = board.check_and_forward(&from_black, BLACK);
        board.print_board();
		gui.push(format!("B{}", from_black));

        // check victory
        if valid != 4 {
            if server::is_winner(valid,&from_black, &(black_player.color)) {
                gui.push("BWIN".to_string());
                black_player.push("WIN".to_string());
                white_player.push("LOSE".to_string());
            }
            else {
                gui.push("WWIN".to_string());
                black_player.push("LOSE".to_string());
                white_player.push("WIN".to_string());
            }
            break;
        }

        // push white
		white_player.push(from_black.clone());
        
        // pull white
        let now = Instant::now();
        let from_white = white_player.pull();
        let elapse = now.elapsed().as_millis();
        if elapse < 3000 {
            thread::sleep(Duration::from_millis((3000 - elapse) as u64));
        }
		println!("White: {}", &from_white) ;
		
        // add board & push gui
        let valid = board.check_and_forward(&from_white, WHITE);
        board.print_board();
        gui.push(format!("W{}", from_white.clone()));

        // check victory
        if valid != 4 {
            if server::is_winner(valid, &from_white, &(white_player.color)) {
                gui.push("WWIN".to_string());
                black_player.push("LOSE".to_string());
                white_player.push("WIN".to_string());
            }
            else {
                gui.push("BWIN".to_string());
                black_player.push("WIN".to_string());
                white_player.push("LOSE".to_string());
            }
            break;
        }

        // push black
        black_player.push(from_white);

		thread::sleep(Duration::from_secs(1)) ;
    }

    gui.thread.join().unwrap();
    black_player.thread.join().unwrap();
    white_player.thread.join().unwrap();
    println!("Program Terminating");
}
