within MainLibrary.Components;
model Heater
  Modelica.Thermal.HeatTransfer.Interfaces.HeatPort_a port_a
    annotation (Placement(transformation(extent={{90,-10},{110,10}})));
  Modelica.Thermal.HeatTransfer.Sources.PrescribedHeatFlow prescribedHeatFlow
    annotation (Placement(transformation(extent={{50,-10},{70,10}})));
  Modelica.Blocks.Sources.CombiTimeTable heating_table
    annotation (Placement(transformation(extent={{0,-10},{20,10}})));
equation
  connect(prescribedHeatFlow.port, port_a)
    annotation (Line(points={{70,0},{100,0}}, color={191,0,0}));
  connect(heating_table.y[1], prescribedHeatFlow.Q_flow)
    annotation (Line(points={{21,0},{50,0}}, color={0,0,127}));
  annotation (Icon(coordinateSystem(preserveAspectRatio=false)), Diagram(
        coordinateSystem(preserveAspectRatio=false)));
end Heater;
