within CustomerLibrary.TestLibrary.Test2;
model TestMdl2
  CustomerLibrary.Components.Heater heater(heating_table(
      tableOnFile=true,
      tableName="heater",
      fileName=Modelica.Utilities.Files.loadResource("modelica://CustomerLibrary/Resources/Data/heating.txt")))
    annotation (Placement(transformation(extent={{-40,-10},{-20,10}})));
  Modelica.Thermal.HeatTransfer.Components.HeatCapacitor heatCapacitor(C=5)
    annotation (Placement(transformation(extent={{-10,0},{10,20}})));
  CustomerLibrary.Components.AmbientLosses ambientLosses(ambient_G(
      tableOnFile=true,
      tableName="lossG",
      fileName=Modelica.Utilities.Files.loadResource("modelica://CustomerLibrary/Resources/Data/losses.txt")))
    annotation (Placement(transformation(extent={{20,-10},{40,10}})));
equation
  connect(heater.port_a, heatCapacitor.port)
    annotation (Line(points={{-20,0},{0,0}}, color={191,0,0}));
  connect(ambientLosses.port_b, heatCapacitor.port)
    annotation (Line(points={{20.2,0},{0,0}}, color={191,0,0}));
  annotation (
    Icon(coordinateSystem(preserveAspectRatio=false)),
    Diagram(coordinateSystem(preserveAspectRatio=false)),
    experiment(StopTime=100));
end TestMdl2;
