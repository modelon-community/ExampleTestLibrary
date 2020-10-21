within RegressionTests;
function startRegressionTest
extends RegressionTesting.startRegressionTest(
    testTarget="RegressionTests",
    folderToStoreIn=1,
    pathRefFiles=Modelica.Utilities.Files.loadResource("modelica://RegressionTests/") + "../../ReferenceFiles",
    referenceOptions=2,
    AdvancedStoreProtectedVariables=true,
    CPUTimeAsPlot=true,
    verifyDefinedTrajectoryPaths=true);

  annotation (
    Documentation(revisions="<html>
<COPYRIGHT>
</html>"));
end startRegressionTest;
