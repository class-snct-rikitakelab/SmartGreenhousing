int sensorPin = A0;
double moistValue = 0;
double moist;

void setup() {
  Serial.begin(9600);
}

void loop() {
  moistValue = analogRead(sensorPin);
  moist = moistValue / 1023 * 100;
  Serial.print(moist);
  Serial.println("%");

  delay(100);
}
