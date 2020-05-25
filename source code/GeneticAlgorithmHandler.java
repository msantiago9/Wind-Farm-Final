/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package windfarm;

import java.util.ArrayList;

public class GeneticAlgorithmHandler {

    static ArrayList<WindFarm> population;
    static ArrayList<WindFarm> breedingPool;

    public static void start() {
        System.out.println("Intializing...");
        initialize();
        int generation = 0;
        while (generation < WindFarmPane.numGenerations) {
            System.out.println("Generation: " + generation);
            System.out.println("Generating Breeding Pool...");
            generateBreedingPool();
            System.out.println("Generating Children...");
            generateChildren();
            System.out.println("Creating Mutations...");
            createMutations();
            generation++;
            updateScreen(generation);
        }
    }

    public static void initialize() {
        population = new ArrayList<WindFarm>();
        for (int i = 0; i < WindFarmPane.maxPopulation; i++) {
            population.add(new WindFarm(WindFarmPane.numTurbines));
        }
    }

    public static void generateBreedingPool() {
        breedingPool = new ArrayList<WindFarm>();
        double averageFitness = 0;
        for (WindFarm wf : population) {
            averageFitness += wf.getFitness();
        }
        averageFitness /= population.size(); // 0.5
        for (WindFarm wf : population) {
            wf.calculateFitness();
            double currentFitness = wf.getFitness();
            if (wf.getFitness() >= averageFitness*4.5/5.0) {
                while (currentFitness > 0) {
                    breedingPool.add(new WindFarm(wf));
                    currentFitness -= averageFitness / 5;
                }
            }
        }
    }

    public static void generateChildren() {
        ArrayList<WindFarm> nextGeneration = new ArrayList<WindFarm>();
        while (nextGeneration.size() < population.size()) {
            WindFarm parent1 = breedingPool.get(((int) (Math.random() * breedingPool.size())));
            WindFarm parent2 = breedingPool.get(((int) (Math.random() * breedingPool.size())));
            nextGeneration.add(new WindFarm(parent1, parent2));
        }
        population = nextGeneration;
    }

    public static void createMutations() {
        for (WindFarm wf : population) {
            double random = Math.random();
            if (random <= WindFarmPane.mutationRate) { //right number should be the mutation rate
                double before = wf.getFitness();
                wf.mutate();
                double after = wf.getFitness();
            }
        }
    }

    public static WindFarm getBestFarm() {
        double maxFit = 0;
        double current;
        int index = 0;
        String fitnesses = "";
        for (int i = 0; i < population.size(); i++) {
            current = population.get(i).getFitness();
            fitnesses += current + "\n";
            if (current > maxFit) {
                maxFit = current;
                index = i;
            }
        }
        return population.get(index);
    }

    public static void updateScreen(int generation) {
        WindFarmMain.window.updateGeneration(generation, getBestFarm());
    }
}
