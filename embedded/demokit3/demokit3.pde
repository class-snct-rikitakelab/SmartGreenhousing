#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

int delaytime=100;


#define  LED3_RED       2
#define  LED3_GREEN     4
#define  LED3_BLUE      3

#define  LED2_RED       5
#define  LED2_GREEN     7
#define  LED2_BLUE      6

#define  LED1_RED       8
#define  LED1_GREEN     10
#define  LED1_BLUE      9

#define  LED0_RED       11
#define  LED0_GREEN     13
#define  LED0_BLUE      12

#define  RELAY1         A0
#define  RELAY2         A1

#define  LIGHT_SENSOR   A2
#define  TEMP_SENSOR    A3

#define  BUTTON1        A6
#define  BUTTON2        A7
#define  BUTTON3        A8

AndroidAccessory acc("Google, Inc.",
		     "DemoKit",
		     "DemoKit Arduino Board",
		     "1.0",
		     "http://www.android.com",
		     "0000000012345678");

void setup();
void loop();


void init_buttons()
{
	pinMode(BUTTON1, INPUT);
	pinMode(BUTTON2, INPUT);
	pinMode(BUTTON3, INPUT);

	// enable the internal pullups
	digitalWrite(BUTTON1, HIGH);
	digitalWrite(BUTTON2, HIGH);
	digitalWrite(BUTTON3, HIGH);
}


void init_relays()
{
	pinMode(RELAY1, OUTPUT);
	pinMode(RELAY2, OUTPUT);
}


void init_leds()
{
        digitalWrite(LED0_RED, 0);
	digitalWrite(LED0_GREEN, 0);
	digitalWrite(LED0_BLUE, 0);

	pinMode(LED0_RED, OUTPUT);
	pinMode(LED0_GREEN, OUTPUT);
	pinMode(LED0_BLUE, OUTPUT);  
  
	digitalWrite(LED1_RED, 0);
	digitalWrite(LED1_GREEN, 0);
	digitalWrite(LED1_BLUE, 0);

	pinMode(LED1_RED, OUTPUT);
	pinMode(LED1_GREEN, OUTPUT);
	pinMode(LED1_BLUE, OUTPUT);

	digitalWrite(LED2_RED, 0);
	digitalWrite(LED2_GREEN, 0);
	digitalWrite(LED2_BLUE, 0);

	pinMode(LED2_RED, OUTPUT);
	pinMode(LED2_GREEN, OUTPUT);
	pinMode(LED2_BLUE, OUTPUT);

	digitalWrite(LED3_RED, 0);
	digitalWrite(LED3_GREEN, 0);
	digitalWrite(LED3_BLUE, 0);

	pinMode(LED3_RED, OUTPUT);
	pinMode(LED3_GREEN, OUTPUT);
	pinMode(LED3_BLUE, OUTPUT);
}

//void init_joystick(int threshold);

byte b1, b2, b3, b4, c;
void setup()
{
	Serial.begin(115200);
	Serial.print("\r\nStart");

	init_leds();
	init_relays();
	init_buttons();


	b1 = digitalRead(BUTTON1);
	b2 = digitalRead(BUTTON2);
	b3 = digitalRead(BUTTON3);
	c = 0;

	acc.powerOn();
}

void loop()
{
	byte err;
	byte idle;
	static byte count = 0;
	byte msg[3];
	long touchcount;

	if (acc.isConnected()) {
		int len = acc.read(msg, sizeof(msg), 1);
		int i;
		byte b;
		uint16_t val;
		int x, y;
		char c0;

		if (len > 0) {
			// assumes only one command per packet
			if (msg[0] == 0x2) {
				if (msg[1] == 0x0)
					analogWrite(LED1_RED, msg[2]);
				else if (msg[1] == 0x1)
					analogWrite(LED1_GREEN, msg[2]);
				else if (msg[1] == 0x2)
					analogWrite(LED1_BLUE, msg[2]);
				else if (msg[1] == 0x3)
					analogWrite(LED2_RED, msg[2]);
				else if (msg[1] == 0x4)
					analogWrite(LED2_GREEN, msg[2]);
				else if (msg[1] == 0x5)
					analogWrite(LED2_BLUE, msg[2]);
				else if (msg[1] == 0x6)
					analogWrite(LED3_RED, msg[2]);
				else if (msg[1] == 0x7)
					analogWrite(LED3_GREEN, msg[2]);
				else if (msg[1] == 0x8)
					analogWrite(LED3_BLUE, msg[2]);
			} else if (msg[0] == 0x3) {
				if (msg[1] == 0x0)
					digitalWrite(RELAY1, msg[2] ? HIGH : LOW);
				else if (msg[1] == 0x1)
					digitalWrite(RELAY2, msg[2] ? HIGH : LOW);
			}
		}

		msg[0] = 0x1;

		b = digitalRead(BUTTON1);
		if (b != b1) {
			msg[1] = 0;
			msg[2] = b ? 0 : 1;
			acc.write(msg, 3);
			b1 = b;
		}

		b = digitalRead(BUTTON2);
		if (b != b2) {
			msg[1] = 1;
			msg[2] = b ? 0 : 1;
			acc.write(msg, 3);
			b2 = b;
		}

		b = digitalRead(BUTTON3);
		if (b != b3) {
			msg[1] = 2;
			msg[2] = b ? 0 : 1;
			acc.write(msg, 3);
			b3 = b;
		}

		switch (count++ % 0x10) {
		case 0:
			val = analogRead(TEMP_SENSOR);
			msg[0] = 0x4;
			msg[1] = val >> 8;
			msg[2] = val & 0xff;
			acc.write(msg, 3);
			break;

		case 0x4:
			val = analogRead(LIGHT_SENSOR);
			msg[0] = 0x5;
			msg[1] = val >> 8;
			msg[2] = val & 0xff;
			acc.write(msg, 3);
			break;

		}
	} else {
		// reset outputs to default values on disconnect
		analogWrite(LED1_RED, 0);
		analogWrite(LED1_GREEN, 0);
		analogWrite(LED1_BLUE, 0);
		analogWrite(LED2_RED, 0);
		analogWrite(LED2_GREEN, 0);
		analogWrite(LED2_BLUE, 0);
		analogWrite(LED3_RED, 0);
		analogWrite(LED3_GREEN, 0);
		analogWrite(LED3_BLUE, 0);
		digitalWrite(RELAY1, LOW);
		digitalWrite(RELAY2, LOW);
	}

	delay(delaytime);
}
