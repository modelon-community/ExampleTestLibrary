within LocalTests.Test1;
model TestMdl1
  extends CustomerLibrary.TestLibrary.Test1.TestMdl1;
  annotation (
    Icon(coordinateSystem(preserveAspectRatio=false)),
    Diagram(coordinateSystem(preserveAspectRatio=false)),
    experiment(StopTime=100));
	
	parameter Integer year(fixed=false);
	parameter Integer month(fixed=false);
	parameter Integer day(fixed=false);
	parameter Integer hour(fixed=false);
	parameter Integer minute(fixed=false);
	parameter Integer second(fixed=false);
	parameter Integer millisecond(fixed=false);
	
initial algorithm
  (millisecond, second, minute, hour, day, month, year) = Modelica.Utilities.System.getTime();
  
  Modelica.Utilities.Streams.print(String(year) + "." + String(month) + "." + String(day) + " - " + String(hour) + ":" + String(minute) + ":" + String(second) + ":" + String(millisecond) + " - " + Modelica.Utilities.System.getWorkDirectory(), "C:/Users/JesseGohl/working/junk.txt");
  
end TestMdl1;
