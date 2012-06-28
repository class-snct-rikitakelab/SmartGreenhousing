#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define  MOIST_SENSOR   A2
#define  TEMP_SENSOR    A3

AndroidAccessory acc("Google, Inc.",
		     "DemoKit",
		     "DemoKit Arduino Board",
		     "1.0",
		     "http://www.android.com",
		     "0000000012345678");
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
    delay(100);      
    val = analogRead(MOIST_SENSOR);
    msg[0] = 0x5;
    msg[1] = val >> 8;
    msg[2] = val & 0xff;
    acc.write(msg, 3);
  }
  delay(100);
}
