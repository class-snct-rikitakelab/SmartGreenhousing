#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#define  DELAY_SENSOR   A0
#define  MOIST_SENSOR   A2
#define  TEMP_SENSOR    A3

AndroidAccessory acc("Google, Inc.",
		     "DemoKit",
		     "DemoKit Arduino Board",
		     "1.0",
		     "http://www.android.com",
		     "0000000012345678");
int delay_time=100;

void setup()
{
	Serial.begin(115200);
	acc.powerOn();
}

void loop()
{
  byte msg[3];
  int val;
  int vt=10;
  
  vt = analogRead(DELAY_SENSOR);
  delay_time = 10 + vt*3;
  
  if (acc.isConnected()) {
    
    msg[0] = 0x1;
    msg[1] = delay_time >> 8;
    msg[2] = delay_time & 0xff;
    acc.write(msg, 3);
    
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
  }
  delay(delay_time);
}
