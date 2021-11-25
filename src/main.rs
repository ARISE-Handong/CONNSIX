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
// use board_operation::{Board, WHITE, BLACK};
// use board_operation::WHITE;
// use board_operation::BLACK;
// use board_operation::Board;

use server::UserInterface;
use server::Player;
use server::Color;

fn main() {

    // GUI 연결
    let stdin = std::io::stdin();
    let mut iterator = stdin.lock().lines();
    
    // println!("Enter IP for Players: ");
    // let ip_player = iterator.next().unwrap().unwrap(); // standard input으로 ip 받기
    let ip_player = String::from("192.168.0.9"); // 자기 고정 ip

    println!("Enter PORT for GUI: ");
    let port = iterator.next().unwrap().unwrap().parse::<u16>().unwrap();
    let ip_gui = String::from("127.0.0.1");
    let gui = UserInterface::new(ip_gui, port);
    
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
    // let ip = String::from("127.0.0.1");
    let (black_player, white_player) = server::connect_player(ip_player, black_port, white_port);

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

        if(from_black.contains("30 seconds")) {
            println!("White Won!");
            gui.push("WWIN".to_string());
            black_player.push("LOSE".to_string());
            white_player.push("WIN".to_string());
            break;
        }
		println!("Black: {}", &from_black);

        // add board & push gui
        let valid = board.check_and_forward(&from_black, BLACK);
        board.print_board();
		gui.push(format!("B{}", from_black));

        // check victory
        match valid {
            CheckResult::ForwardMsg => (), // continue game
            CheckResult::GameEnd => { // black won
                println!("Black Won!");
                gui.push("BWIN".to_string());
                black_player.push("WIN".to_string());
                white_player.push("LOSE".to_string());
                break;
            },
            CheckResult::InvalidInput => { // white won
                println!("Received Invalid Input [{}] from Black", from_black);
                gui.push("WWIN".to_string());
                black_player.push("LOSE".to_string());
                white_player.push("WIN".to_string());
                break;
            }
            CheckResult::ErrorMsg => { // white won
                println!("Received Error Message [{}] from Black", from_black);
                gui.push("WWIN".to_string());
                black_player.push("LOSE".to_string());
                white_player.push("WIN".to_string());
                break;
            }
            CheckResult::FullBoard => { // Tie
                println!("The Board is full!");
                gui.push("BTIE".to_string());
                black_player.push("TIE".to_string());
                white_player.push("TIE".to_string());
                break;
            },
        }

        // push white
		white_player.push(from_black.clone());
        
        // pull white
        let now = Instant::now();
        let from_white = white_player.pull();
        let elapse = now.elapsed().as_millis();
        if elapse < interval {
            thread::sleep(Duration::from_millis((interval - elapse) as u64));
        }
        if(from_white.contains("30 seconds")) {
            println!("Black Won!");
            gui.push("BWIN".to_string());
            black_player.push("WIN".to_string());
            white_player.push("LOSE".to_string());
            break;
        }
		println!("White: {}", &from_white) ;
		
        // add board & push gui
        let valid = board.check_and_forward(&from_white, WHITE);
        board.print_board();
        gui.push(format!("W{}", from_white.clone()));

        // check victory
        match valid {
            CheckResult::ForwardMsg => (), // continue game
            CheckResult::GameEnd => { // white won
                println!("White Won!");
                gui.push("WWIN".to_string());
                black_player.push("LOSE".to_string());
                white_player.push("WIN".to_string());
                break;
            },
            CheckResult::InvalidInput => { // black won
                println!("Received Invalid Input [{}] from White", from_white);
                gui.push("BWIN".to_string());
                black_player.push("WIN".to_string());
                white_player.push("LOSE".to_string());
                break;
            }
            CheckResult::ErrorMsg => { // black won
                println!("Received Error Message [{}] from White", from_white);
                gui.push("BWIN".to_string());
                black_player.push("WIN".to_string());
                white_player.push("LOSE".to_string());
                break;
            }
            CheckResult::FullBoard => { // Tie
                println!("The Board is full!");
                gui.push("BTIE".to_string());
                black_player.push("TIE".to_string());
                white_player.push("TIE".to_string());
                break;
            },
        }

        // push black
        black_player.push(from_white);

        // server::is_winner(valid, "hi", &(Color::Black));
    }

    gui.thread.join().unwrap();
    // black_player.thread.join().unwrap();
    // white_player.thread.join().unwrap();
    println!("Program Terminating");
}

// pub fn is_winner(valid: CheckResult, msg: &str, color: &Color, gui: UserInterface, black_player: Player, white_player: Player) -> (UserInterface, Player, Player) {

//     match valid {
//         CheckResult::ForwardMsg => (gui, black_player, white_player), // continue game
//         CheckResult::GameEnd => { // white won
//             println!("{}} Won!", color);
//             gui.push("WWIN".to_string());
//             black_player.push("LOSE".to_string());
//             white_player.push("WIN".to_string());
//             (gui, black_player, white_player)
//         },
//         CheckResult::InvalidInput => { // black won
//             println!("Received Invalid Input [{}] from {}", color);
//             gui.push("BWIN".to_string());
//             black_player.push("WIN".to_string());
//             white_player.push("LOSE".to_string());
//             (gui, black_player, white_player)
//         }
//         CheckResult::ErrorMsg => { // black won
//             println!("Received Error Message [{}] from {}}", color);
//             gui.push("BWIN".to_string());
//             black_player.push("WIN".to_string());
//             white_player.push("LOSE".to_string());
//             (gui, black_player, white_player)
//         }
//         CheckResult::FullBoard => { // Tie
//             println!("The Board is full!");
//             gui.push("BTIE".to_string());
//             black_player.push("TIE".to_string());
//             white_player.push("TIE".to_string());
//             (gui, black_player, white_player)
//         },
//     }
// }