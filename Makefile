n=10
s=5
e=10
t=100
repeats=1
p=g6
m=sparse_landmarks
em=Diagonal
fps=1000

all: compile

compile:
	javac scout/sim/Simulator.java

gui:
	java scout.sim.Simulator --verbose --fps ${fps} --gui -p ${p} -m ${m} -em ${em} -n ${n} -e ${e} -s ${s} -t ${t}

run:
	java scout.sim.Simulator -S 201 -r ${repeats} -p ${p} -m ${m} -em ${em} -n ${n} -e ${e} -s ${s} -t ${t}

verbose:
	java scout.sim.Simulator -p ${p} -m ${m} -em ${em} -n ${n} -e ${e} -s ${s} -t ${t} --verbose
