
public class Connect6 {

	// AI턴에서는 returnPoint로 받아오기

	static int color = DummyAI.getMyColor();
	static int opponent = DummyAI.getYourColor();
	static int red = DummyAI.getRedColor();
	private static int[][] playBoard = new int[19][19];
	private static int[][] origin = new int[19][19];
	static boolean count = false;

	// 중간에 보드 임시로 원본이랑 끊어진,,그거...
	private static void getTempBoard(int x, int y) {
		playBoard[x][y] = color;
	}

	// integer 형태의 이상적 좌표를 형식에 맞춘 String으로 바꿔 리턴(다음에 놓을 그거임. 스톤 하나하나 기준.)
	public static String returnStringCoor() {
		

		for (int Y = 18; Y >= 0; Y--) {
			System.out.printf("%2d ", Y + 1);
			for (int X = 0; X < 19; X++) {
				playBoard[X][Y] = origin[X][Y];
				if(playBoard[X][Y] != 0) weight[X][Y] = -10000;
				System.out.printf("[%3d]", playBoard[X][Y]);
			}
			System.out.println("");
		}

		System.out.print("   ");
		for (int o = 0; o < 19; o++) {
			System.out.printf("  %c  ", 65 + o);
		}
		System.out.println("\n");

		returnPoint();
		// x, y를 바탕으로 String형태의 머시깽이...
		String stone1 = String.format("%c%02d", (char) ((x < 8) ? (x + 'A') : (x + 'A' + 1)), y + 1);

		 weight[x][y] = -10000;
		// 일단여기는절대두지말라는뜻!!!! board를 직접 수정하면 NOTEMPTY에러가 나서 임시방편으로...
		getTempBoard(x, y);
		// 방금 자기가 놓은거 업데이트해주고

		returnPoint();
		
		weight[x][y] = -10000;
		String stone2 = String.format("%c%02d", (char) ((x < 8) ? (x + 'A') : (x + 'A' + 1)), y + 1);

		String result = stone1 + ":" + stone2;

		System.out.println(result);
		// showWeight();

		return result;

	}

	// 현재 산출된 이상적 좌표
	static int x;
	static int y;
	// 가중치 설정을 위한 배열
	static int[][] weight = new int[19][19]; // 일반가중치(계속 누적)
	static int[][] superWeight = new int[19][19]; // 특수가중치(매 분석마다 리셋후 시작)

	// 판 읽고 특수가중치 누적하기
	private static void addSuperWeight() {

		// 특수가중치 판 초기화
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				superWeight[i][j] = 0;
			}
		}

		int myCount = 0, yourCount = 0, emptyCount = 0, check;

		//// 놓으면 이길 때(한방승리)
		// -------------------------------------------------------------------------------------

		// 세로 공격 시작점
		// -------------------------------------------------------------------------------------------------

		for (int X = 0; X < 19; X++) { // 0~18
			for (int Y = 0; Y < 14; Y++) { // 0~12

				myCount = 0;
				emptyCount = 0;

				for (int k = 0; k < 6; k++) { // 13~18
					if (playBoard[X][Y + k] == color)
						myCount++;
					else if (playBoard[X][Y + k] == 0)
						emptyCount++;
				}

				// 칠목방지
				if ((Y - 1 < 0 || playBoard[X][Y - 1] == color) || (Y + 6 > 18 || playBoard[X][Y + 6] == color))
					continue;

				if (myCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) { // 13~18

						if (playBoard[X][Y + k] == 0) {
							if (myCount == 5) {
								superWeight[X][Y + k] += 999;
							} else if (myCount == 4 && count == false) {
								superWeight[X][Y + k] += 999;
								return;
							} else if (myCount == 3) {
								superWeight[X][Y + k] += 100;
							}
						}
					}
				}

			}
		}

		// 가로 공격 시작점
		// -------------------------------------------------------------------------------------------------
		for (int Y = 0; Y < 19; Y++) {
			for (int X = 0; X < 14; X++) {

				myCount = 0;
				emptyCount = 0;

				for (int k = 0; k < 6; k++) {
					if (playBoard[X + k][Y] == color)
						myCount++;
					else if (playBoard[X + k][Y] == 0)
						emptyCount++;
				}

				// 7mok nono
				if ((X - 1 < 0 || playBoard[X - 1][Y] == color) || (X + 6 > 18 || playBoard[X + 6][Y] == color))
					continue;

				if (myCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) { // 13~18
						if (playBoard[X + k][Y] == 0) {
							if (myCount == 5)
								superWeight[X + k][Y] += 999;
							else if (myCount == 4 && count == false) {
								superWeight[X + k][Y] += 999;
								return;
							} else if (myCount == 3) {
								superWeight[X + k][Y] += 100;
							}
						}
					}
				}

			}
		}

		// 좌대각 공격 시작점
		// ----------------------------------------------------------------------
		for (int X = 0; X < 14; X++) { // 0~13
			for (int Y = 5; Y < 19; Y++) { // 5~18

				myCount = 0;
				emptyCount = 0;

				for (int k = 0; k < 6; k++) { // 13~18, 본인부터 본인+5, 왼쪽아래로 내려가니까 좌대각 \
					if (playBoard[X + k][Y - k] == color)
						myCount++;
					else if (playBoard[X + k][Y - k] == 0)
						emptyCount++;
				}

				// 칠목방지, 인덱스범위
				if (((X - 1 < 0 || Y + 1 > 18) || playBoard[X - 1][Y + 1] == color)
						|| ((X + 6 > 18 || Y - 6 < 0) || playBoard[X + 6][Y - 6] == color))
					continue;

				if (myCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) { // 빈칸에 가중치 쏴주고 리턴
						if (playBoard[X + k][Y - k] == 0) {
							if (myCount == 5)
								superWeight[X + k][Y - k] += 999;
							else if (myCount == 4 && count == false) {
								superWeight[X + k][Y - k] += 999;
								return;
							} else if (myCount == 3) {
								superWeight[X + k][Y - k] += 100;
							}
						}
					}
				}
			}
		}

		// 우대각 공격 시작점
		// ----------------------------------------------------------------------
		for (int X = 5; X < 19; X++) { // 5~18
			for (int Y = 5; Y < 19; Y++) { // 5~18

				myCount = 0;
				emptyCount = 0;

				for (int k = 0; k < 6; k++) { // 13~18, 본인부터...오른쪽아래로 내려가니까 우대각 /
					if (playBoard[X - k][Y - k] == color)
						myCount++;
					else if (playBoard[X - k][Y - k] == 0)
						emptyCount++;
				}

				// 칠목방지 + 인덱스범위
				if (((X + 1 > 18 || Y + 1 > 18) || playBoard[X + 1][Y + 1] == color)
						|| ((X - 6 < 0 || Y - 6 < 0) || playBoard[X - 6][Y - 6] == color))
					continue;

				if (myCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) { // 빈칸에 가중치 쏴주고 리턴
						if (playBoard[X - k][Y - k] == 0) {
							if (myCount == 5)
								superWeight[X - k][Y - k] += 999;
							if (myCount == 4 && count == false) {
								superWeight[X - k][Y - k] += 999;
								return;
							} else if (myCount == 3) {
								superWeight[X - k][Y - k] += 100;
							}
						}
					}
				}
			}
		}

		//// 안놓으면 질 때, 한방방어
		//// ----------------------------------------------------------------------------------

		// 가로 방어 시작점 ------------------------------------------------------

				for (int Y = 0; Y < 19; Y++) {
					for (int X = 0; X < 14; X++) {
						myCount = 0;
						check = 0;
						// 본인기준 여섯개 체크
						for (int k = 0; k < 6; k++) {
							try {
								// 상대 돌의 연속점? 세는거같은데 중간에 자기 색이 나오면 세던거 엎어버림.
								if (playBoard[X + k][Y] == opponent) {
									myCount++;
									System.out.println("가로 mycount = " + myCount + " X = " + (X + k) + " Y = " + Y + " k = " + k + "\ncheck = " + check);
								} else if (playBoard[X + k][Y] == color || playBoard[X + k][Y] == red) {
									myCount = 0;
									break;
								}
								// 비어있는부분 만나면 체크를.. 그 비어있는곳 앞에 몇개의 상대돌이 연속되어있는지 저장해주는건가봐.
								else if (playBoard[X + k][Y] == 0) {
									if (check == 0 && myCount != 0)
										check = myCount;
								}

							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}

						// System.out.println("가로 방어로 진입." + i+" : "+j);

						if (myCount == 5) {
							try {
								// 연속
								if (check == 5) {
									if (playBoard[X][Y] == 0 && playBoard[X - 1][Y] != opponent) {
										if (playBoard[X + 6][Y] == 0) {
											superWeight[X][Y] += 999;
											superWeight[X + 6][Y] += 999;
											return;
										} else if (playBoard[X + 6][Y] != opponent) {
											superWeight[X][Y] += 999;
											return;
										}
									} else if (playBoard[X + 5][Y] == 0 && playBoard[X + 6][Y] != opponent) {
										if (playBoard[X - 1][Y] == 0) {
											superWeight[X - 1][Y] += 999;
											superWeight[X + 5][Y] += 999;
											return;
										} else if ((playBoard[X - 1][Y] == color || playBoard[X - 1][Y] == red) && playBoard[X + 6][Y] != opponent) {
											superWeight[X + 5][Y] += 999;
											return;
										}
									}
								}

								// 중간에 공백 있음
								else if (playBoard[X - 1][Y] != opponent && playBoard[X + 6][Y] != opponent) {
									for (int k = 0; k < 6; k++) {
										if (playBoard[X + k][Y] == 0) {
											superWeight[X + k][Y] += 999;
											return;
										}
									}
								}

							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}

						// 4연
						else if (myCount == 4) {
							try {

								if (check == 4 || check == 0) {
									for (int k = 0; k < 6; k++) {
										// 4개 연속
										if (playBoard[X + k][Y] == opponent) {
											if (playBoard[X + k - 1][Y] == 0 && playBoard[X + k + 4][Y] == 0) {
												superWeight[X + k - 1][Y] += 999;
												superWeight[X + k + 4][Y] += 999;
												return;
											} else if ((playBoard[X + k - 1][Y] == color || playBoard[X + k - 1][Y] == red) && playBoard[X + k + 4][Y] == 0) {
												superWeight[X + k + 4][Y] += 999;
												return;
											} else if ((playBoard[X + k + 4][Y] == color || playBoard[X + k + 4][Y] == red) && playBoard[X + k - 1][Y] == 0) {
												superWeight[X + k - 1][Y] += 999;
												return;
											}
											break;
										}
									}
								}

								else if (check == 3) {
									for (int k = 0; k < 6; k++) {
										// 3연속 1 공백 1
										if (playBoard[X + k][Y] == opponent && playBoard[X + k + 4][Y] == opponent) {
											if (playBoard[X + k - 1][Y] == 0 && playBoard[X + k + 5][Y] == 0) {
												superWeight[X + k - 1][Y] += 999;
												superWeight[X + k + 5][Y] += 999;
												return;
											} else if (k == 0 && (playBoard[X + k - 1][Y] == color || playBoard[X + k - 1][Y] == red)) {
												superWeight[X + k + 5][Y] += 999;
												return;
											} else if (k == 1 && (playBoard[X + k + 5][Y] == color || playBoard[X + k + 5][Y] == red)) {
												superWeight[X + k - 1][Y] += 999;
												return;
											}
											break;
										}
										// 3연속 2공백 1
										else if (playBoard[X + k][Y] == opponent && playBoard[X + k + 3][Y] == 0
												&& playBoard[X + k + 4][Y] == 0) {
											if (weight[X + k + 3][Y] > weight[X + k + 4][Y]) {
												superWeight[X + k + 3][Y] += 999;
											} else {
												superWeight[X + k + 4][Y] += 999;
											}
											return;
										}
									}
								}

								else if (check == 2) {
									for (int k = 0; k < 6; k++) {
										if (playBoard[X + k][Y] == opponent) {
											// 2연속 1공백 2
											if (playBoard[X + k - 1][Y] == 0 && playBoard[X + k + 5][Y] == 0) {
												superWeight[X + k - 1][Y] += 999;
												superWeight[X + k + 5][Y] += 999;
												return;
											} else if (playBoard[X + k + 6][Y] != opponent && k == 0 && (playBoard[X + k - 1][Y] == color || playBoard[X + k - 1][Y] == red)) {
												superWeight[X + k + 5][Y] += 999;
												return;
											} else if (playBoard[X + k - 2][Y] != opponent && k == 1 && (playBoard[X + k + 5][Y] == color || playBoard[X + k + 5][Y] == red)) {
												superWeight[X + k - 1][Y] += 999;
												return;
											}
											else if (playBoard[X + k + 2][Y] == 0 && playBoard[X + k + 4][Y] == 0
													&& playBoard[X + k - 1][Y] != opponent && playBoard[X + k + 6][Y] != opponent) {
												if (weight[X + k + 2][Y] > weight[X + k + 4][Y]) {
													superWeight[X + k + 2][Y] += 999;
												} else {
													superWeight[X + k + 4][Y] += 999;
												}
												return;
											}
											// 2연속 2공백 2
											else if (playBoard[X + k + 2][Y] == 0 && playBoard[X + k + 3][Y] == 0) {
												if (weight[X + k + 2][Y] > weight[X + k + 3][Y]) {
													superWeight[X + k + 2][Y] += 999;
												} else {
													superWeight[X + k + 3][Y] += 999;
												}
												return;
											}
											break;
										}
									}
								}

								else if (check == 1) {
									for (int k = 0; k < 6; k++) {
										// 1 1공백 3연속
										if (playBoard[X + k][Y] == opponent && playBoard[X + k + 2][Y] == opponent) {
											if (playBoard[X + k - 1][Y] == 0 && playBoard[X + k + 5][Y] == 0) {
												superWeight[X + k - 1][Y] += 999;
												superWeight[X + k + 5][Y] += 999;
												return;
											} else if (k == 0 && (playBoard[X + k - 1][Y] == color || playBoard[X + k - 1][Y] == red)) {
												superWeight[X + k + 5][Y] += 999;
												return;
											} else if (k == 1 && (playBoard[X + k + 5][Y] == color || playBoard[X + k + 5][Y] == red)) {
												superWeight[X + k - 1][Y] += 999;
												return;
											}
											break;
										}
										// 1 2공백 3연속
										else if (playBoard[X + k][Y] == opponent && (playBoard[X + k - 1][Y] != opponent && playBoard[X + k + 6][Y] != opponent)) {
											if(playBoard[X + k + 2][Y] == 0) {
												if (weight[X + k + 1][Y] > weight[X + k + 2][Y]) {
													superWeight[X + k + 1][Y] += 999;
												} else {
													superWeight[X + k + 2][Y] += 999;
												}
											} else {
												if (weight[X + k + 1][Y] > weight[X + k + 3][Y]) {
													superWeight[X + k + 1][Y] += 999;
												} else {
													superWeight[X + k + 3][Y] += 999;
												}
											}
											return;
										}
										// 1 공백 1 공백 2연
										else if (playBoard[X + k][Y] == opponent) {
											
										}
									}
								}

							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						} else if (myCount == 3) {
							for (int k = 0; k < 6; k++) {
								if (playBoard[X + k][Y] == 0)
									superWeight[X + k][Y] += 100;
							}
						}

					}
				}

				// 세로 방어 시작점 ------------------------------------------------------

				for (int Y = 0; Y < 14; Y++) {
					for (int X = 0; X < 19; X++) { // 14넘으면 여섯개 범위 잡을 때 어차피 인덱스 에러 나니깐...
						myCount = 0;
						check = 0;
						// 본인기준 여섯개 체크
						for (int k = 0; k < 6; k++) {
							try {
								// 상대 돌의 연속점? 세는거같은데 중간에 자기 색이 나오면 세던거 엎어버림.
								if (playBoard[X][Y + k] == opponent) {
									myCount++;
									System.out.println("세로 mycount = " + myCount + " X = " + X + " Y = " + (Y + k) + " k = " + k + "\ncheck = " + check);
								} else if (playBoard[X][Y + k] == color || playBoard[X][Y + k] == red) {
									myCount = 0;
									break;
								}
								// 비어있는부분 만나면 체크를.. 그 비어있는곳 앞에 몇개의 상대돌이 연속되어있는지 저장해주는건가봐.
								else if (playBoard[X][Y + k] == 0) {
									if (check == 0 && myCount != 0)
										check = myCount;
								}

							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}

						if (myCount == 5) {
							try {
								// 연속
								if (check == 5) {
									if (playBoard[X][Y] == 0 && playBoard[X][Y - 1] != opponent) {
										if (playBoard[X][Y + 6] == 0) {
											superWeight[X][Y] += 999;
											superWeight[X][Y + 6] += 999;
											return;
										} else if (playBoard[X][Y + 6] != opponent) {
											superWeight[X][Y] += 999;
											return;
										}
									} else if (playBoard[X][Y + 5] == 0 && playBoard[X][Y + 6] != opponent) {
										if (playBoard[X][Y - 1] == 0) {
											superWeight[X][Y - 1] += 999;
											superWeight[X][Y + 5] += 999;
											return;
										} else if ((playBoard[X][Y - 1] == color || playBoard[X][Y - 1] == red) && playBoard[X][Y + 6] != opponent) {
											superWeight[X][Y + 5] += 999;
											return;
										}
									}
								}

								// 중간에 공백 있음
								else if (playBoard[X][Y - 1] != opponent && playBoard[X][Y + 6] != opponent) {
									for (int k = 0; k < 6; k++) {
										if (playBoard[X][Y + k] == 0) {
											superWeight[X][Y + k] += 999;
											return;
										}
									}
								}

							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}

						// 4연
						else if (myCount == 4) {
							try {

								if (check == 4 || check == 0) {
									for (int k = 0; k < 6; k++) {
										// 4개 연속
										System.out.println("세로 4개 연속");
										if (playBoard[X][Y + k] == opponent) {
											System.out.println("시작점 X = " + X + " Y = " + (Y + k));
											if (playBoard[X][Y + k - 1] == 0 && playBoard[X][Y + k + 4] == 0) {
												superWeight[X][Y + k - 1] += 999;
												superWeight[X][Y + k + 4] += 999;
												return;
											} else if ((playBoard[X][Y + k - 1] == color || playBoard[X][Y + k - 1] == red) && playBoard[X][Y + k + 4] == 0) {
												superWeight[X][Y + k + 4] += 999;
												return;
											} else if ((playBoard[X][Y + k + 4] == color || playBoard[X][Y + k + 4] == red) && playBoard[X][Y + k - 1] == 0) {
												superWeight[X][Y + k - 1] += 999;
												return;
											}
											break;
										}
									}
								}

								else if (check == 3) {
									for (int k = 0; k < 6; k++) {
										// 3연속 1 공백 1
										if (playBoard[X][Y + k] == opponent && playBoard[X][Y + k + 4] == opponent) {
											if (playBoard[X][Y + k - 1] == 0 && playBoard[X][Y + k + 5] == 0) {
												superWeight[X][Y + k - 1] += 999;
												superWeight[X][Y + k + 5] += 999;
												return;
											} else if (k == 0 && (playBoard[X][Y + k - 1] == color || playBoard[X][Y + k - 1] == red)) {
												superWeight[X][Y + k + 5] += 999;
												return;
											} else if (k == 1 && (playBoard[X][Y + k + 5] == color || playBoard[X][Y + k + 5] == red)) {
												superWeight[X][Y + k - 1] += 999;
												return;
											}
											break;
										}
										// 3연속 2공백 1
										else if (playBoard[X][Y + k] == opponent && playBoard[X][Y + k + 3] == 0
												&& playBoard[X][Y + k + 4] == 0) {
											if (weight[X][Y + k + 3] > weight[X][Y + k + 4]) {
												superWeight[X][Y + k + 3] += 999;
											} else {
												superWeight[X][Y + k + 4] += 999;
											}
											return;
										}
									}
								}

								else if (check == 2) {
									for (int k = 0; k < 6; k++) {
										if (playBoard[X][Y + k] == opponent) {
											// 2연속 1공백 2
											if (playBoard[X][Y + k - 1] == 0 && playBoard[X][Y + k + 5] == 0) {
												superWeight[X][Y + k - 1] += 999;
												superWeight[X][Y + k + 5] += 999;
												return;
											} else if (playBoard[X][Y + k + 6] != opponent && k == 0 && (playBoard[X][Y + k - 1] == color || playBoard[X][Y + k - 1] == red)) {
												superWeight[X][Y + k + 5] += 999;
												return;
											} else if (playBoard[X][Y + k - 2] != opponent && k == 1 && (playBoard[X][Y + k + 5] == color || playBoard[X][Y + k + 5] == red)) {
												superWeight[X][Y + k - 1] += 999;
												return;
											}
											else if (playBoard[X][Y + k + 2] == 0 && playBoard[X][Y + k + 4] == 0
													&& playBoard[X][Y + k - 1] != opponent && playBoard[X][Y + k + 6] != opponent) {
												if (weight[X][Y + k + 2] > weight[X][Y + k + 4]) {
													superWeight[X][Y + k + 2] += 999;
												} else {
													superWeight[X][Y + k + 4] += 999;
												}
												return;
											}
											// 2연속 2공백 2
											else if (playBoard[X][Y + k + 2] == 0 && playBoard[X][Y + k + 3] == 0) {
												if (weight[X][Y + k + 2] > weight[X][Y + k + 3]) {
													superWeight[X][Y + k + 2] += 999;
												} else {
													superWeight[X][Y + k + 3] += 999;
												}
												return;
											}
											break;
										}
									}
								}

								else if (check == 1) {
									for (int k = 0; k < 6; k++) {
										// 1 1공백 3연속
										if (playBoard[X][Y + k] == opponent && playBoard[X][Y + k + 2] == opponent) {
											if (playBoard[X][Y + k - 1] == 0 && playBoard[X][Y + k + 5] == 0) {
												superWeight[X][Y + k - 1] += 999;
												superWeight[X][Y + k + 5] += 999;
												return;
											} else if (k == 0 && (playBoard[X][Y + k - 1] == color || playBoard[X][Y + k - 1] == red)) {
												superWeight[X][Y + k + 5] += 999;
												return;
											} else if (k == 1 && (playBoard[X][Y + k + 5] == color || playBoard[X][Y + k + 5] == red)) {
												superWeight[X][Y + k - 1] += 999;
												return;
											}
											break;
										}
										// 1 2공백 3연속
										else if (playBoard[X][Y + k] == opponent && (playBoard[X][Y + k - 1] != opponent && playBoard[X][Y + k + 6] != opponent)) {
											if (playBoard[X][Y + k + 2] == 0) {
												if (weight[X][Y + k + 1] > weight[X][Y + k + 2]) {
													superWeight[X][Y + k + 1] += 999;
												} else {
													superWeight[X][Y + k + 2] += 999;
												}
											} else {
												if (weight[X][Y + k + 1] > weight[X][Y + k + 3]) {
													superWeight[X][Y + k + 1] += 999;
												} else {
													superWeight[X][Y + k + 3] += 999;
												}
											}
											return;
										}
									}
								}

							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " +Y);
							}
						} else if (myCount == 3) {
							for (int k = 0; k < 6; k++) {
								if (playBoard[X][Y + k] == 0)
									superWeight[X][Y + k] += 100;
							}
						}

					}
				}


		// 좌대각)\) 방어 시작점
		// ------------------------------------------------------------------------

		for (int Y = 18; Y > 4; Y--) {
			for (int X = 0; X < 14; X++) {
				myCount = 0;
				check = 0;
				for (int k = 0; k < 6; k++) {
					try {
						if (playBoard[X + k][Y - k] == opponent) {
							myCount++;
							System.out.println("좌대각 mycount = " + myCount + " X = " + (X + k) + " Y = " + (Y - k)
									+ " k = " + k + "\ncheck = " + check);
						} else if (playBoard[X + k][Y - k] == 0) {
							if (check == 0 && myCount != 0)
								check = myCount;
						} else {
							myCount = 0;
							break;
						}

					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("index error " + X + " " + Y);
					}
				}
				// 5 왼쪽 위에서 오른쪽 아래(좌대각\) 방어
				if (myCount == 5) {
					try {
						// 연속
						if (check == 5) {
							try {
								if (playBoard[X][Y] == 0 && playBoard[X - 1][Y + 1] != opponent) {
									if (playBoard[X + 6][Y - 6] == 0) {
										superWeight[X][Y] += 999;
										superWeight[X + 6][Y + 6] += 999;
										return;
									} else if (playBoard[X + 6][Y - 6] != opponent) {
										superWeight[X][Y] += 999;
										return;
									}
								} else if (playBoard[X + 5][Y - 5] == 0 && playBoard[X + 6][Y - 6] != opponent) {
									if (playBoard[X - 1][Y + 1] == 0) {
										superWeight[X - 1][Y + 1] += 999;
										superWeight[X + 5][Y - 5] += 999;
										return;
									} else if (playBoard[X - 1][Y + 1] == color || playBoard[X - 1][Y + 1] == red) {
										superWeight[X + 5][Y - 5] += 999;
										return;
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
						// 중간에 공백 있음
						else if (playBoard[X - 1][Y + 1] != opponent && playBoard[X + 6][Y - 6] != opponent) {
							for (int k = 0; k < 6; k++) {
								if (playBoard[X + k][Y - k] == 0) {
									superWeight[X + k][Y - k] += 999;
									return;
								}
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("index error " + X + " " + Y);
					}
				} else if (myCount == 4) {
					if (check == 0 || check == 4) {
						for (int k = 0; k < 6; k++) {
							try {
								System.out.println("좌대각 4개 연속");
								// 4개 연속
								if (playBoard[X + k][Y - k] == opponent) {
									if (playBoard[X + k - 1][Y - k + 1] == 0 && playBoard[X + k + 4][Y - k - 4] == 0) {
										superWeight[X + k - 1][Y - k + 1] += 999;
										superWeight[X + k + 4][Y - k - 4] += 999;
										System.out.println("좌대각 양쪽 뚫림");
										return;
									} else if ((playBoard[X + k - 1][Y - k + 1] == color
											|| playBoard[X + k - 1][Y - k + 1] == red)
											&& playBoard[X + k + 4][Y - k - 4] == 0) {
										superWeight[X + k + 4][Y - k - 4] += 999;
										System.out.println("좌대각 우측 뚫림");
										return;
									} else if ((playBoard[X + k + 4][Y - k - 4] == color
											|| playBoard[X + k + 4][Y - k - 4] == red)
											&& playBoard[X + k - 1][Y - k + 1] == 0) {
										superWeight[X + k - 1][Y - k + 1] += 999;
										System.out.println("좌대각 좌측 뚫림");
										return;
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
					} else if (check == 3) {
						for (int k = 0; k < 6; k++) {
							try {
								// 3연속 1 공백 1
								if (playBoard[X + k][Y - k] == opponent
										&& playBoard[X + k + 4][Y - k - 4] == opponent) {
									if (playBoard[X + k - 1][Y - k + 1] == 0 && playBoard[X + k + 5][Y - k + 5] == 0) {
										superWeight[X + k - 1][Y - k + 1] += 999;
										superWeight[X + k + 5][Y - k + 5] += 999;
										return;
									} else if (k == 0 && (playBoard[X + k - 1][Y - k + 1] == color
											|| playBoard[X + k - 1][Y - k + 1] == red)) {
										superWeight[X + k + 5][Y - k - 5] += 999;
										return;
									} else if (k == 1 && (playBoard[X + k + 5][Y - k - 5] == color
											|| playBoard[X + k + 5][Y - k - 5] == red)) {
										superWeight[X + k - 1][Y - k + 1] += 999;
										return;
									}
								}
								// 3연속 2공백 1
								else if (playBoard[X + k][Y - k] == opponent && playBoard[X + k + 3][Y - k - 3] == 0
										&& playBoard[X + k + 4][Y - k - 4] == 0) {
									if (weight[X + k + 3][Y - k - 3] > weight[X + k + 4][Y - k - 4]) {
										superWeight[X + k + 3][Y - k - 3] += 999;
									} else {
										superWeight[X + k + 4][Y - k - 4] += 999;
									}
									return;
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
					} else if (check == 2) {
						for (int k = 0; k < 6; k++) {
							try {
								if (playBoard[X + k][Y - k] == opponent) {
									// 2연속 1공백 2
									if (playBoard[X + k - 1][Y - k + 1] == 0 && playBoard[X + k + 5][Y - k - 5] == 0) {
										superWeight[X + k - 1][Y - k + 1] += 999;
										superWeight[X + k + 5][Y - k - 5] += 999;
										return;
									} else if (k == 0
											&& (playBoard[X + k - 1][Y - k + 1] == color
													|| playBoard[X + k - 1][Y - k + 1] == red)
											&& playBoard[X + k + 6][Y + k - 6] != opponent
											&& playBoard[X + k + 5][Y - k - 5] == 0) {
										superWeight[X + k + 5][Y - k - 5] += 999;
										return;
									} else if (k == 1
											&& (playBoard[X + k + 5][Y - k - 5] == color
													|| playBoard[X + k + 5][Y - k - 5] == red)
											&& playBoard[X + k - 2][Y + k + 2] != opponent) {
										superWeight[X + k - 1][Y - k + 1] += 999;
										return;
									}
									//2연속 1공백 1 1공백 1
									else if(playBoard[X + k + 2][Y - k - 2] == 0 && playBoard[X + k + 4][Y - k - 4] == 0 && playBoard[X + k - 1][Y - k + 1] != opponent && playBoard[X + k + 6][Y - k - 6] != opponent) {
										if (weight[X + k + 2][Y - k - 2] > weight[X + k + 4][Y - k - 4]) {
											superWeight[X + k + 2][Y - k - 2] += 999;
										} else {
											superWeight[X + k + 4][Y - k - 4] += 999;
										}
										return;
									}
									// 2연속 2공백 2
									else if (playBoard[X + k + 2][Y - k - 2] == 0
											&& playBoard[X + k + 3][Y - k - 3] == 0) {
										if (weight[X + k + 2][Y - k - 2] > weight[X + k + 3][Y - k - 3]) {
											superWeight[X + k + 2][Y - k - 2] += 999;
										} else {
											superWeight[X + k + 3][Y - k - 3] += 999;
										}
										return;
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
					} else if (check == 1) {
						for (int k = 0; k < 6; k++) {
							try {
								// 1 1공백 3연속
								if (playBoard[X + k][Y - k] == opponent
										&& playBoard[X + k + 2][Y - k - 2] == opponent) {
									if (playBoard[X + k - 1][Y - k + 1] == 0 && playBoard[X + k + 5][Y - k - 5] == 0) {
										superWeight[X + k - 1][Y - k + 1] += 999;
										superWeight[X + k + 5][Y - k - 5] += 999;
										return;
									} else if (k == 0 && (playBoard[X + k - 1][Y - k + 1] == color
											|| playBoard[X + k - 1][Y - k + 1] == red)) {
										superWeight[X + k + 5][Y - k - 5] += 999;
										return;
									} else if (k == 1 && (playBoard[X + k + 5][Y - k - 5] == color
											|| playBoard[X + k + 5][Y - k - 5] == red)) {
										superWeight[X + k - 1][Y - k + 1] += 999;
										return;
									}
								}
								// 1 2공백 3연속
								else if (playBoard[X + k][Y - k] == opponent
										&& playBoard[X + k - 1][Y - k + 1] != opponent
										&& playBoard[X + k + 6][Y - k + 6] != opponent) {
									if (playBoard[X + k + 2][Y - k - 2] == 0) {
										if (weight[X + k + 1][Y - k - 1] > weight[X + k + 2][Y - k - 2]) {
											superWeight[X + k + 1][Y - k - 1] += 999;
										} else {
											superWeight[X + k + 2][Y - k - 2] += 999;
										}
										return;
									} else if (playBoard[X + k + 2][Y - k - 2] == opponent) {
										if (weight[X + k + 1][Y - k - 1] > weight[X + k + 3][Y - k - 3]) {
											superWeight[X + k + 1][Y - k - 1] += 999;
										} else {
											superWeight[X + k + 3][Y - k - 3] += 999;
										}
										return;
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
					}
				} else if (myCount == 3) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X + k][Y - k] == 0) {
							superWeight[X + k][Y - k] += 100;
						}
					}
				}
			}
		}

		// 우대각 방어 시작점
		// -------------------------------------------------------------------------------

		for (int Y = 0; Y < 14; Y++) {
			for (int X = 0; X < 14; X++) {
				myCount = 0;
				check = 0;
				for (int k = 0; k < 6; k++) {
					try {
						if (playBoard[X + k][Y + k] == opponent) {
							myCount++;
							System.out.println("우대각 mycount = " + myCount + " X = " + (X + k) + " Y = " + (Y + k)
									+ " k = " + k + "\ncheck = " + check);
						} else if (playBoard[X + k][Y + k] == 0) {
							if (check == 0 && myCount != 0)
								check = myCount;
						} else {
							myCount = 0;
							break;
						}

					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("index error " + X + " " + Y);
					}
				}
				if (myCount == 5) {
					try {
						// 5개 연속
						if (check == 5) {
							try {
								if (playBoard[X][Y] == 0 && playBoard[X - 1][Y - 1] != opponent) {
									if (playBoard[X + 6][Y + 6] == 0) {
										superWeight[X][Y] += 999;
										superWeight[X + 6][Y + 6] += 999;
										return;
									} else if (playBoard[X + 6][Y + 6] != opponent) {
										superWeight[X][Y] += 999;
										return;
									}
								} else if (playBoard[X + 5][Y + 5] == 0 && playBoard[X + 6][Y + 6] != opponent) {
									if (playBoard[X - 1][Y - 1] == 0) {
										superWeight[X - 1][Y - 1] += 999;
										superWeight[X + 5][Y + 5] += 999;
										return;
									} else if (playBoard[X - 1][Y - 1] == color || playBoard[X - 1][Y - 1] == red) {
										superWeight[X + 5][Y + 5] += 999;
										return;
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
						// 중간에 공백 있음
						else if (playBoard[X - 1][Y - 1] != opponent && playBoard[X + 6][Y + 6] != opponent) {
							for (int k = 0; k < 6; k++) {
								if (playBoard[X + k][Y + k] == 0) {
									superWeight[X + k][Y + k] += 999;
									return;
								}
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("index error " + X + " " + Y);
					}
				} else if (myCount == 4) {
					if (check == 0 || check == 4) {
						for (int k = 0; k < 6; k++) {
							try {
								System.out.println("우대각 4개 연속");
								// 4개 연속
								if (playBoard[X + k][Y + k] == opponent) {
									if (playBoard[X + k - 1][Y + k - 1] == 0 && playBoard[X + k + 4][Y + k + 4] == 0) {
										superWeight[X + k - 1][Y + k - 1] += 999;
										superWeight[X + k + 4][Y + k + 4] += 999;
										System.out.println("우대각 양쪽 뚫림");
										return;
									} else if ((playBoard[X + k - 1][Y + k - 1] == color
											|| playBoard[X + k - 1][Y + k - 1] == red)
											&& playBoard[X + k + 4][Y + k + 4] == 0) {
										superWeight[X + k + 4][Y + k + 4] += 999;
										System.out.println("우대각 우쪽 뚫림");
										return;
									} else if ((playBoard[X + k + 4][Y + k + 4] == color
											|| playBoard[X + k + 4][Y + k + 4] == red)
											&& playBoard[X + k - 1][Y + k - 1] == 0) {
										superWeight[X + k - 1][Y + k - 1] += 999;
										System.out.println("우대각 좌쪽 뚫림");
										return;
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
					} else if (check == 3) {
						for (int k = 0; k < 6; k++) {
							try {
								// 3연속 1 공백 1
								if (playBoard[X + k][Y + k] == opponent
										&& playBoard[X + k + 4][Y + k + 4] == opponent) {
									if (playBoard[X + k - 1][Y + k - 1] == 0 && playBoard[X + k + 5][Y + k + 5] == 0) {
										superWeight[X + k - 1][Y + k - 1] += 999;
										superWeight[X + k + 5][Y + k + 5] += 999;
										return;
									} else if (k == 0 && (playBoard[X + k - 1][Y + k - 1] == color
											|| playBoard[X + k - 1][Y + k - 1] == red)) {
										superWeight[X + k + 5][Y + k + 5] += 999;
										return;
									} else if (k == 1 && (playBoard[X + k + 5][Y + k + 5] == color
											|| playBoard[X + k + 5][Y + k + 5] == red)) {
										superWeight[X + k - 1][Y + k - 1] += 999;
										return;
									}
								}
								// 3연속 2공백 1
								else if (playBoard[X + k][Y + k] == opponent && playBoard[X + k + 3][Y + k + 3] == 0
										&& playBoard[X + k + 4][Y + k + 4] == 0) {
									if (weight[X + k + 3][Y + k + 3] > weight[X + k + 4][Y + k + 4]) {
										superWeight[X + k + 3][Y + k + 3] += 999;
									} else {
										superWeight[X + k + 4][Y + k + 4] += 999;
									}
									return;
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
					} else if (check == 2) {
						for (int k = 0; k < 6; k++) {
							try {
								if (playBoard[X + k][Y + k] == opponent) {
									// 2연속 1공백 2 1공백
									if (playBoard[X + k - 1][Y + k - 1] == 0 && playBoard[X + k + 5][Y + k + 5] == 0) {
										superWeight[X + k - 1][Y + k - 1] += 999;
										superWeight[X + k + 5][Y + k + 5] += 999;
										return;
									} else if (k == 0
											&& (playBoard[X + k - 1][Y + k - 1] == color
													|| playBoard[X + k - 1][Y + k - 1] == red)
											&& playBoard[X + k + 6][Y + k + 6] != opponent) {
										superWeight[X + k + 5][Y + k + 5] += 999;
										return;
									} else if (k == 1
											&& (playBoard[X + k + 5][Y + k + 5] == color
													|| playBoard[X + k + 5][Y + k + 5] == red)
											&& playBoard[X + k - 2][Y + k - 2] != opponent) {
										superWeight[X + k - 1][Y + k - 1] += 999;
										return;
									}
									else if(playBoard[X + k + 2][Y + k + 2] == 0 && playBoard[X + k + 4][Y + k + 4] == 0 && playBoard[X + k - 1][Y + k - 1] != opponent && playBoard[X + k + 6][Y + k + 6] != opponent) {
										if (weight[X + k + 2][Y + k + 2] > weight[X + k + 4][Y + k + 4]) {
											superWeight[X + k + 2][Y + k + 2] += 999;
										} else {
											superWeight[X + k + 4][Y + k + 4] += 999;
										}
										return;
									}
									// 2연속 2공백 2
									else if (playBoard[X + k + 2][Y + k + 2] == 0
											&& playBoard[X + k + 3][Y + k + 3] == 0) {
										if (weight[X + k + 2][Y + k + 2] > weight[X + k + 3][Y + k + 3]) {
											superWeight[X + k + 2][Y + k + 2] += 999;
										} else {
											superWeight[X + k + 3][Y - k + 3] += 999;
										}
										return;
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
					} else if (check == 1) {
						for (int k = 0; k < 6; k++) {
							try {
								// 1 1공백 3연속
								if (playBoard[X + k][Y + k] == opponent
										&& playBoard[X + k + 2][Y + k + 2] == opponent) {
									if (playBoard[X + k - 1][Y + k - 1] == 0 && playBoard[X + k + 5][Y + k + 5] == 0) {
										superWeight[X + k - 1][Y + k - 1] += 999;
										superWeight[X + k + 5][Y + k + 5] += 999;
										return;
									} else if (k == 0 && (playBoard[X + k - 1][Y + k - 1] == color
											|| playBoard[X + k - 1][Y + k - 1] == red)) {
										superWeight[X + k + 5][Y + k + 5] += 999;
										return;
									} else if (k == 1 && (playBoard[X + k + 5][Y + k + 5] == color
											|| playBoard[X + k + 5][Y + k + 5] == red)) {
										superWeight[X + k - 1][Y + k - 1] += 999;
										return;
									}
								}
								// 1 2공백 3연속
								else if (playBoard[X + k][Y + k] == opponent
										&& playBoard[X + k - 1][Y + k - 1] != opponent
										&& playBoard[X + k + 6][Y + k + 6] != opponent) {
									if (playBoard[X + k + 2][Y + k + 2] == 0) {
										if (weight[X + k + 1][Y + k + 1] > weight[X + k + 2][Y + k + 2]) {
											superWeight[X + k + 1][Y + k + 1] += 999;
										} else {
											superWeight[X + k + 2][Y + k + 2] += 999;
										}
										return;
									} else if (playBoard[X + k + 2][Y + k + 2] == red) {
										if (weight[X + k + 1][Y + k + 1] > weight[X + k + 3][Y + k + 3]) {
											superWeight[X + k + 1][Y + k + 1] += 999;
										} else {
											superWeight[X + k + 3][Y + k + 3] += 999;
										}
										return;
									}

								}
							} catch (ArrayIndexOutOfBoundsException e) {
								System.out.println("index error " + X + " " + Y);
							}
						}
					}
				} else if (myCount == 3) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X + k][Y + k] == 0)
							superWeight[X + k][Y + k] += 100;
					}
				}
			}
		}
		addDefaultWeight();
	}

	private static void addDefaultWeight() {

		int myCount, yourCount, emptyCount, redCount;

		// 세로 시작점 ----------------------------------------------------------------------
		for (int X = 0; X < 19; X++) { // 0~18
			for (int Y = 0; Y < 14; Y++) { // 0~12

				myCount = 0;
				yourCount = 0;
				emptyCount = 0;
				redCount = 0;

				for (int k = 0; k < 6; k++) { // 13~18
					if (playBoard[X][Y + k] == color)
						myCount++;
					else if (playBoard[X][Y + k] == 0)
						emptyCount++;
					else if (playBoard[X][Y + k] == opponent)
						yourCount++;
					else if (playBoard[X][Y + k] == red)
						redCount++;
				}

				if (myCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X][Y + k] == 0)
							superWeight[X][Y + k] += (myCount * 10);
					}
				} else if (yourCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X][Y + k] == 0)
							superWeight[X][Y + k] += (yourCount * 3);
					}
				}
				if (redCount != 0) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X][Y + k] == red)
							superWeight[X][Y + k] -= (redCount * 5);
					}
				}

			}
		}

		// 가로 시작점
		// -------------------------------------------------------------------------------------------------
		for (int Y = 0; Y < 19; Y++) {
			for (int X = 0; X < 14; X++) {

				myCount = 0;
				yourCount = 0;
				emptyCount = 0;
				redCount = 0;

				for (int k = 0; k < 6; k++) {
					if (playBoard[X + k][Y] == color)
						myCount++;
					else if (playBoard[X + k][Y] == 0)
						emptyCount++;
					else if (playBoard[X + k][Y] == opponent)
						yourCount++;
					else if (playBoard[X + k][Y] == red)
						redCount++;
				}

				if (myCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X + k][Y] == 0)
							superWeight[X + k][Y] += (myCount * 10);
					}
				} else if (yourCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X + k][Y] == 0)
							superWeight[X + k][Y] += (yourCount * 3);
					}
				} else if (redCount != 0) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X + k][Y] == 0)
							superWeight[X + k][Y] -= (redCount * 5);
					}
				}

			}
		}

		// 좌대각 시작점
		// ----------------------------------------------------------------------
		for (int X = 0; X < 14; X++) { // 0~13
			for (int Y = 5; Y < 19; Y++) { // 5~18

				myCount = 0;
				yourCount = 0;
				emptyCount = 0;
				redCount = 0;

				for (int k = 0; k < 6; k++) { // 13~18, 본인부터 본인+5, 왼쪽아래로 내려가니까 좌대각 \
					if (playBoard[X + k][Y - k] == color)
						myCount++;
					else if (playBoard[X + k][Y - k] == 0)
						emptyCount++;
					else if (playBoard[X + k][Y - k] == opponent)
						yourCount++;
					else if (playBoard[X + k][Y - k] == red)
						redCount++;
				}

				if (myCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X + k][Y - k] == 0)
							superWeight[X + k][Y - k] += (myCount * 10);
					}
				} else if (yourCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X + k][Y - k] == 0)
							superWeight[X + k][Y - k] += (yourCount * 3);
					}
				} else if (redCount != 0) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X + k][Y - k] == 0)
							superWeight[X + k][Y - k] -= (redCount * 5);
					}
				}

			}
		}

		// 우대각 시작점
		// ----------------------------------------------------------------------
		for (int X = 5; X < 19; X++) { // 5~18
			for (int Y = 5; Y < 19; Y++) { // 5~18

				myCount = 0;
				yourCount = 0;
				emptyCount = 0;
				redCount = 0;

				for (int k = 0; k < 6; k++) { // 13~18, 본인부터...오른쪽아래로 내려가니까 우대각 /
					if (playBoard[X - k][Y - k] == color)
						myCount++;
					else if (playBoard[X - k][Y - k] == 0)
						emptyCount++;
					else if (playBoard[X - k][Y - k] == opponent)
						yourCount++;
					else if (playBoard[X - k][Y - k] == red)
						redCount++;
				}

				if (myCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X - k][Y - k] == 0)
							superWeight[X - k][Y - k] += (myCount * 10);
					}
				} else if (yourCount + emptyCount == 6) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X - k][Y - k] == 0)
							superWeight[X - k][Y - k] += (yourCount * 3);
					}
				} else if (redCount != 0) {
					for (int k = 0; k < 6; k++) {
						if (playBoard[X - k][Y - k] == 0)
							superWeight[X - k][Y - k] -= (redCount * 5);
					}
				}

			}
		}

		System.out.println("Default weight added.");

	}

	// 일반가중치+특수가중치 판에서 최대 가중치를 찾아 x,y 값 저장해주기
	public static void returnPoint() {
		System.out.println("before addSuperWeight\n");

		addSuperWeight();
		count = !count;
		System.out.println("done addsuperWeight");
		// showWeight();

		int max = 0;
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				if (superWeight[i][j] + weight[i][j] > max) {
					max = superWeight[i][j] + weight[i][j];
					x = i;
					y = j;
				}
			}
		}

		showWeight();

	}

	// 현재 가중치 상태 콘솔에 출력
	public static void showWeight() {
		for (int i = 18; i >= 0; i--) {
			System.out.printf("%2d ", i + 1);
			for (int j = 0; j < 19; j++) {
				System.out.printf("[%3d]", weight[j][i] + superWeight[j][i]);
			}
			System.out.println("");
		}
		System.out.print("   ");
		for (int o = 0; o < 19; o++) {
			System.out.printf("  %c  ", 65 + o);
		}
		System.out.println("\n");
	}

}