from pyfmi import load_fmu
from oct.steadystate.nlesol import FMUProblem, Solver, SolverOptions
import os
import glob
import shutil

def run(path_spec, test_configuration, test_condition):
    label = "Stationary solve"
    fmu_list = glob.glob(path_spec['case_artifact_path'] + "/*.fmu")
    fmu = load_fmu(fmu_list[0])
    try:
        p = FMUProblem(fmu, list(fmu.get_states_list().keys()), list(fmu.get_derivatives_list().keys()))

        s = Solver(p)

        report = s.get_log_filename().split('/')[-1]
        opts = SolverOptions()
        opts['silent_mode'] =True
        s.solve(options=opts)
        status = "success"
    except Exception as exc:
        try:
            with open(report, 'a+') as outfile:
                outfile.write("="*80 + "\nException:\n")
                outfile.write(str(exc))
        except:
            pass
        status = "fail"


    return {'label':label,'status': status,'report': report}