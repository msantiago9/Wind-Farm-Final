/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package windfarm;

import java.util.ArrayList;
import java.util.Arrays;

public class WindFarm {

    private ArrayList<Turbine> turbines;

    private int[][] coordinates;

    private double fitness;

    public WindFarm(int n) {
        generateRandomWindFarm(n);
        calculateFitness();
        updateCoordinates();
    }

    public WindFarm(WindFarm parent) {
        this.turbines = new ArrayList<Turbine>(parent.getWindFarm());
        this.fitness = parent.getFitness();
        updateCoordinates();
    }

    public WindFarm(ArrayList<Turbine> turbines) {
        this.turbines = new ArrayList<Turbine>(turbines);
        calculateFitness();
        updateCoordinates();
    }

    public WindFarm(WindFarm parent1, WindFarm parent2) {

        turbines = new ArrayList<Turbine>();

        coordinates = new int[1 + WindFarmPane.height / 100][1 + WindFarmPane.width / 100];
        Turbine current;
        int x, y;

        for (double rand = Math.random(); turbines.size() < WindFarmPane.numTurbines; rand = Math.random()) {
            if (rand < 0.5) { // parent1's genes
                current = new Turbine(parent1.getWindFarm().get((int) (Math.random() * parent1.getWindFarm().size())));

            } else { //parent2's genes
                current = new Turbine(parent2.getWindFarm().get((int) (Math.random() * parent2.getWindFarm().size())));
            }

            y = current.getY() / 100;
            x = current.getX() / 100;

            if (coordinates[y][x] == 0) {
                coordinates[y][x] = 1;
                turbines.add(current);
            }
        }
        calculateFitness();
    }

    public void updateCoordinates() {
        coordinates = new int[1 + WindFarmPane.height / 100][1 + WindFarmPane.width / 100];
        for (Turbine t : turbines) {
            int y = t.getY() / 100;
            int x = t.getX() / 100;
            coordinates[y][x] = 1;
        }
    }

    //number specifies the number of turbines on this generated wind farm
    public void generateRandomWindFarm(int number) {
        coordinates = new int[1 + WindFarmPane.height / 100][1 + WindFarmPane.width / 100];
        turbines = new ArrayList<Turbine>();

        while (turbines.size() < number) { //Sets the initial positions of the turbines.
            int x = (((int) (Math.random() * WindFarmPane.width)) / 100);
            int y = (((int) (Math.random() * WindFarmPane.height)) / 100);

            if (coordinates[y][x] == 0) {
                coordinates[y][x] = 1;
                turbines.add(new Turbine(x * 100, y * 100));
            }
        }
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void calculateFitness() {
        int option = WindFarmPane.option;
        switch (option) {
            case 0:
                this.fitness = 1;
                break;
            case 1:
                this.fitness = getCostFitness();
                break;
            case 2:
                this.fitness = getPerformanceFitness();
                break;
            case 3:
                this.fitness = getCostFitness() + getPerformanceFitness();
                break;
        }
    }

    private double getCostFitness() {
        double costFitness = 0;

        //calculating total distance between nodes
        int totalDistance = 0, x2, x1, y2, y1, minX = WindFarmPane.width, minY = WindFarmPane.height, maxX = 0, maxY = 0, count = 0;
        for (int i = 0; i < turbines.size(); i++) {
            for (int j = i + 1; j < turbines.size(); j++) {
                x2 = turbines.get(j).getX();
                y2 = turbines.get(j).getY();
                x1 = turbines.get(i).getX();
                y1 = turbines.get(i).getY();
                totalDistance += Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
                if (x1 < minX) {
                    x1 = minX;
                }
                if (x2 < minX) {
                    x2 = minX;
                }
                if (x1 > maxX) {
                    x1 = maxX;
                }
                if (x2 > maxX) {
                    x2 = maxX;
                }
                if (y1 < minY) {
                    y1 = minY;
                }
                if (y2 < minY) {
                    y2 = minY;
                }
                if (y1 > maxY) {
                    y1 = maxY;
                }
                if (y2 > maxY) {
                    y2 = maxY;
                }
                count++;
            }
        }

        //              a           b
        //              a           c
        double c3 = totalDistance * (WindFarmPane.highVoltageCost + WindFarmPane.mediumVoltageCost);
        double c1 = totalDistance * (WindFarmPane.transformerPrice + WindFarmPane.numTurbines) / 1000;
        double c2 = WindFarmPane.numHighVoltage * WindFarmPane.highVoltage + WindFarmPane.numMediumVoltage * WindFarmPane.mediumVoltage;
        double currentCost = c1 + c2 + c3;
        double maxCost = WindFarmPane.numHighVoltage * WindFarmPane.highVoltage + WindFarmPane.numMediumVoltage * WindFarmPane.mediumVoltage + count * Math.sqrt(Math.pow(maxX - minX, 2) + Math.pow(maxY - minY, 2)) * (WindFarmPane.highVoltageCost + WindFarmPane.mediumVoltageCost + WindFarmPane.transformerPrice + WindFarmPane.numTurbines) / 1000;
        double objective = maxCost / currentCost;
        return objective;

    }

    private double getPerformanceFitness() {
        double alpha = 0.5 / (Math.log(WindFarmPane.surfaceRoughness / WindFarmPane.hubHeight));
        double r = 10; //radius affected for the turbine downstream, assumed to be 10 meters.
        double[][] windSpeeds = new double[WindFarmPane.height / 100][WindFarmPane.width / 100];
        double totalWind = 0;
        double currentSpeed = WindFarmPane.windSpeed;
        //assuming winds come from the north
        //wind speed has not been tampered from north

        for (int i = 0; i < windSpeeds.length; i++) {
            for (int j = 0; j < windSpeeds[0].length; j++) {
                windSpeeds[i][j] = WindFarmPane.windSpeed;
            }
        }

        //if two turbines have the same x position,
        //the lower one will be affected by the higher one.
        int yStart;
        int yCurrent;
        for (int i = 0; i < windSpeeds.length; i++) {
            for (int j = 0; j < windSpeeds[0].length; j++) {
                yStart = j * 100;
                if (coordinates[i][j] == 1) {
                    for (int k = i + 1; k < windSpeeds.length; k++) {
                        yCurrent = k * 100;
                        windSpeeds[i][k] = Math.abs(windSpeeds[i][j] * (1 - ((2 * WindFarmPane.axialInductionFactor) / Math.pow(1 + alpha * ((yCurrent - yStart) / r), 2))));
                    }
                }
            }
        }

        double pwt; //electrical energy generated by one turbine
        double pwf = 0; //electrical energy generated by the whole farm
        int x, y;

        for (Turbine t : turbines) {
            x = t.getX() / 100;
            y = t.getY() / 100;
            pwt = 0.5 * WindFarmPane.turbineConversionEfficiency * WindFarmPane.airDensity * Math.PI * Math.pow(r, 2) * windSpeeds[y][x];
            pwf += pwt;
        }

        //Here we consider the cost only based on the article's given formula.
        double cost = WindFarmPane.numTurbines * ((2.0 / 3) + (1.0 / 3) * Math.pow(Math.E, -0.00174 * WindFarmPane.numTurbines * WindFarmPane.numTurbines));

        //Here we are obtaining the cost to power output ratio as our fitness.
        double objective = cost / (pwf / 1000);
        if (Double.isInfinite(objective)) {
            objective = 10.0;
        }
        return objective;
    }

    public double getFitness() {
        return this.fitness;
    }

    public ArrayList<Turbine> getWindFarm() {
        return this.turbines;
    }

    public void setWindFarm(ArrayList<Turbine> turbines) {
        this.turbines = turbines;
    }

    public void mutate() {
        this.generateRandomWindFarm(WindFarmPane.numTurbines);
    }

    public int[][] getCoordinates() {
        return this.coordinates;
    }

    public String printLayout() {
        int width = WindFarmPane.width / 100;
        int height = WindFarmPane.height / 100;
        String output = "\nFitness Level: " + fitness;
        output += "\nTurbine Coordinates:\n" + turbines + "\n";
        int[][] pos = new int[height + 1][width + 1];
        for (Turbine t : turbines) {
            pos[(t.getY()) / 100][(t.getX()) / 100] = 1;
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (pos[i][j] == 1) {
                    output += "t ";
                } else {
                    output += "o ";
                }
            }
            output += "\n";
        }
        return output;
    }
}
