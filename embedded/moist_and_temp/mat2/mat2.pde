#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define  MOIST_SENSOR   A2
#define  TEMP_SENSOR    A3
#define  DELAY_SENSOR   A4

AndroidAccessory acc("Google, Inc.",
		     "DemoKit",
		     "DemoKit Arduino Board",
		     "1.0",
		     "http://www.android.com",
		     "0000000012345678");


int getDelayTime()
{
  int delay_time=100;
  int vt=10;
  double i=0;

  vt = analogRead(DELAY_SENSOR);
  i = (double) vt * 1000/1023;
  vt = (int) i;
  
  if(vt<10){
    delay_time = 10;
  }else{
    delay_time = (vt/10)*100;
  }
  return delay_time;
}

void show_delay(){ 
  byte msg[3];
  int val;
  if (acc.isConnected()) {    
    val = getDelayTime();
    msg[0] = 0x1;
    msg[1] = val >> 8;
    msg[2] = val & 0xff;
    acc.write(msg, 3);
  }
}

void wait()
{
  int k;
  int ddt=100;
  
  for(k=0;k<ddt;k+=10){
    ddt=getDelayTime();
    show_delay();
    delay(10);
  }
}

void setup()
{
	Serial.begin(115200);
	acc.powerOn();
}

void loop()
{
  byte msg[3];
  int val;
  
  if (acc.isConnected()) {
    
    val = analogRead(TEMP_SENSOR);
    msg[0] = 0x4;
    msg[1] = val >> 8;
    msg[2] = val & 0xff;
    acc.write(msg, 3);
    
    val = analogRead(MOIST_SENSOR);
    msg[0] = 0x5;
    msg[1] = val >> 8;
    msg[2] = val & 0xff;
    acc.write(msg, 3);
  
  wait();
}
