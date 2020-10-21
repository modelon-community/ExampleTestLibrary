within CustomerLibrary.Components;
model AmbientLosses
  Modelica.Thermal.HeatTransfer.Interfaces.HeatPort_b port_b
    annotation (Placement(transformation(extent={{-108,-10},{-88,10}})));
  Modelica.Thermal.HeatTransfer.Components.Convection convection
    annotation (Placement(transformation(extent={{-60,-10},{-40,10}})));
  Modelica.Thermal.HeatTransfer.Sources.FixedTemperature fixedTemperature(T=
        298.15)
    annotation (Placement(transformation(extent={{10,-10},{-10,10}})));
  Modelica.Blocks.Sources.CombiTimeTable ambient_G
    annotation (Placement(transformation(extent={{-10,30},{-30,50}})));
equation
  connect(convection.solid, port_b)
    annotation (Line(points={{-60,0},{-98,0}}, color={191,0,0}));
  connect(fixedTemperature.port, convection.fluid)
    annotation (Line(points={{-10,0},{-40,0}}, color={191,0,0}));
  connect(ambient_G.y[1], convection.Gc)
    annotation (Line(points={{-31,40},{-50,40},{-50,10}}, color={0,0,127}));
  annotation (Icon(coordinateSystem(preserveAspectRatio=false)), Diagram(
        coordinateSystem(preserveAspectRatio=false)));
end AmbientLosses;
