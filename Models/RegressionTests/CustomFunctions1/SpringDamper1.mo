within RegressionTests.CustomFunctions1;
model SpringDamper1
  extends MainLibrary.TestLibrary.CustomFunctions1.SpringDamper1(inertia(J = 10));
  extends RegressionTesting.Interfaces.Test(testSpecification(
      checkTrajectory=true,
      signals={"inertia.phi","springDamper.tau"},
      stopTime=30,
      numberOfIntervals=500,
      method=17));
end SpringDamper1;
