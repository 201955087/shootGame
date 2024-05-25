package org.test.imscary2;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;

// 이벤트 세세하게 기록하기(현실 시간, 위치 표기 : 육하원칙에 따라서 기록 )
// 이벤트 더 다양하게 선언해놓기

public class eventRecord {
    public static void run(String s, Player p) {
        // 문자열 s와 플레이어 p의 정보(위치, 시간)를 받는 기록 함수
        // 플레이어 기준 발생한 이벤트만 기록해주는 함수 run

        try {
            File t = new File("test.txt");
            // 현재 디렉토리에서 test라는 이름의 txt 파일을 t라고 선언

            BufferedWriter bw = new BufferedWriter(new FileWriter(t,true));
            // 파일 t에 버퍼쓰기 bw를 선언

            float x = (float)p.getLocation().getX();
            float y = (float)p.getLocation().getY();
            float z = (float)p.getLocation().getZ();
            // 인게임 내의 플레이어의 x, y, z 좌표를 각각의 변수들에 받아놓음

            SimpleDateFormat d = new SimpleDateFormat("yyyy.MM.dd - HH : mm : ss");
            String newdate = d.format(new Date());
            // SimpleDateFormat : 원래의 데이터 정보를 내가 보고 싶은 형식으로 포맷하여 출력해주는 놈
            // 현실 년도.월.일 - 시:분:초의 정보를 newdate라는 변수로 선언

            bw.write("위치 - X: " + x + ", Y: " + y + ", Z: " + z);
            bw.newLine();
            bw.write("날짜와 시간(현실 기준) - " + newdate);
            bw.newLine();
            // 위치정보 기록 후 한단 내리기, 날짜 시간 기록 후 한단 내리기
            bw.write(s+ "\n\n");
            // 기록된 이벤트 문자열을 기록 후 한단 내리기
            bw.close();
            //기록 멈춰

        } catch (IOException e) {
            // FileWriter가 생성자 변수에는 없는 파일로 접근하면 파일을 찾지 못하는 에러가 발생
            //그걸 방지하기 위해 catch문으로 에러처리
            e.printStackTrace();
        }
    }
}