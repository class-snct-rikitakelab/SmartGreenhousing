int ir_in = 8;
int pin = 10;
int ir_out = 13;
int val_A;

unsigned int ON_OFF[512] ={
                         912,445, 64, 50, 61,163, 63,162,
                          63, 50, 63, 49, 62, 49, 63, 49,
                          64, 49, 62,163, 63,161, 64, 50,
                          62, 50, 62, 50, 63,162, 63, 50,
                          62,162, 63,162, 64,162, 64,162,
                          63,162, 64,161, 63, 50, 62, 50,
                          62, 49, 63, 49, 64, 49, 62, 50,
                          63, 50, 63, 49, 62,163, 63,161,
                          64,162, 63, 0};

int last = 0;
unsigned long us = micros();

// セットアップ
void setup() {
  Serial.begin(57600);        // シリアル通信速度の設定
  pinMode(ir_in, INPUT);  // 入出力ピンの設定
  pinMode(ir_out, OUTPUT);
  pinMode(pin, INPUT);
  //sendSignal(0);
  val_A = HIGH;
}

// dataからリモコン信号を送信
void sendSignal(char pat) {
  for (int cnt = 0; cnt < 512; cnt++) {
    unsigned long len;
    
    switch (pat){
      case 0:
        len = ON_OFF[cnt]*10;  // dataは10us単位でON/OFF時間を記録している
        break;       
      case 1:
        len = ON_OFF[cnt]*10;
        break;        
      default:     
        break;
    }
    
    if (len == 0) break;      // 0なら終端。
    unsigned long us = micros();
    do {
      digitalWrite(ir_out, 1 - (cnt&1)); // iが偶数なら赤外線ON、奇数なら0のOFFのまま
      delayMicroseconds(8);  // キャリア周波数38kHzでON/OFFするよう時間調整
      digitalWrite(ir_out, 0);
      delayMicroseconds(7);
    } while (long(us + len - micros()) > 0); // 送信時間に達するまでループ
  }
}



void loop() { 
  if(val_A==LOW){
    delay(100);
    int val_B = digitalRead(pin); 
    if(val_B==HIGH){
      //処理
      sendSignal(0);
      Serial.println("ON/OFF!!");
    }
  }  
  val_A = digitalRead(pin);
}
