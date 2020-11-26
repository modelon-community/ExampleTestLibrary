within MainLibrary.TestLibrary.CustomFunctions1;
model SpringDamper1
  Modelica.Mechanics.Rotational.Components.Fixed fixed
    annotation (Placement(transformation(extent={{-70,-30},{-50,-10}})));
  Modelica.Mechanics.Rotational.Components.Inertia inertia
    annotation (Placement(transformation(extent={{20,-10},{40,10}})));
  Modelica.Mechanics.Rotational.Components.SpringDamper springDamper(
    c=10,
    d=1,
    phi_rel0=0.26179938779915)
    annotation (Placement(transformation(extent={{-20,-10},{0,10}})));
equation
  connect(springDamper.flange_b, inertia.flange_a)
    annotation (Line(points={{0,0},{20,0}}, color={0,0,0}));
  connect(springDamper.flange_a, fixed.flange)
    annotation (Line(points={{-20,0},{-60,0},{-60,-20}}, color={0,0,0}));
  annotation (Icon(coordinateSystem(preserveAspectRatio=false)), Diagram(
        coordinateSystem(preserveAspectRatio=false)));
end SpringDamper1;
