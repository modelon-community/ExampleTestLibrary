within RegressionTests.Test3;
model TestMdl1
  extends LocalTests.Test3.TestMdl1;
  extends RegressionTesting.Interfaces.Test(testSpecification(
      checkTrajectory=true,
      signals={"heatCapacitor.T","heater.port_a.Q_flow",
          "ambientLosses.port_b.Q_flow"},
      stopTime=100,
      numberOfIntervals=500,
      method=2,
      tolerance=1e-6));
end TestMdl1;
