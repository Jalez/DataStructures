package rajala.jaakko;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Integer;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;

public class AVLtester
{
    private AVLtree tree = null;
    private LinkedList<Node> union;
    private LinkedList<Node> intersection;
    private LinkedList<Node> symmetricDifference;

    /*Lukee löydettyjen tiedostojen rivit ja siirtää alkiot valueAdder-metodille*/
    private void readInput(String fileName)
    {
        String line;

        System.out.println("Trying to read file: " + fileName + ".txt.");
        try {
            BufferedReader br = new BufferedReader( new FileReader(fileName + ".txt."));
            int row = 0;
            boolean linesLeft = true;
            System.out.println("Reading file " + fileName + ".txt lines...");
            while(linesLeft)
            {
                line=br.readLine();

                if(line == null) {
                    System.out.println();
                    System.out.println("No lines left in file " + fileName + ".txt.");
                    linesLeft = false;
                }
                else {
                    String[] values = line.split("\n");
                    //parseInt jäsentää String-tyypin ja palauttaa sen int-muodossa: "16" -> 16
                    int value = Integer.parseInt(values[0]);
                    System.out.print(value + " ");
                    valueAdder(value, fileName, row);
                }
                row++;
            }
        } catch(IOException e)
        {
            System.out.println("File not found.");;
        }
    }

    /* Lisää arvot AVL-puuhun */
    private void valueAdder(int value, String tableName, int row) {
        String origin = getOrigin(tableName);

        //Luodaan AVLpuu, josta voidaan nopeasti ja helposti hakea kaikki
        //tiedot järjestyksessä pienimmästä arvosta suurimpaan.
        if(tree == null) {
            tree = new AVLtree(value, row, origin);
        }
        else {
            tree.addNode(value, row, origin);
        }
    }

    /* Lukee arvot AVL-puusta ja syöttää nämä linkitettyihin listoihin */
    private void filler() {
        //Päivittää mm. puussa mukana olleen taulukon koon solmujen määrän perusteella.
        tree.updateArray();
        //Pistätä puun solmut pienimmästä suurimpaan järjestyksessä kyseiselle taulukolle.
        tree.putNodesToArray(tree.getRoot());
        Node[] array = tree.getAsArray();
        System.out.println("The numbers on the lists were:");
        System.out.print("( ");
        for(Node node:array) {
            System.out.print(node.getKey() + " ");
            //Lisätään aina joka tapauksessa Unioniin, koska se sisältää kaikki puun arvot
            union.add(node);

            //Jos esiintyi molemmissa tiedostoissa:
            if(node.getOrigin().equals("AB")) {
                //Kyseessä leikkaus
                intersection.add(node);
            }
            //Kyseessä poikkileikkaus
            if(!node.getOrigin().equals("AB")){
                symmetricDifference.add(node);
            }
        }
        System.out.println(")");

        System.out.println("Three tables have been created out of them");
    }

    /* Poistaa kyseisestä linkitetystä listasta kyseisen alkion. Apuna binäärihaku. */
    private boolean removeFromHash(int value, String hashTable) {
        LinkedList<Node> table;
        if(hashTable.equals("or")) {
            System.out.println("Getting here");
            table = union;
        }
        else if(hashTable.equals("and")) {
            table = intersection;
        }
        else if(hashTable.equals("xor")){
            table = symmetricDifference;
        }
        else {
            System.out.println("Couldnt find a table with that name.");
            return false;
        }

        //Käytetään binäärihakua löytämään arvo, koska tämä toimii ajassa O(log n).
        //Ei siis tarvitse käydä jokaista arvoa läpi.
        int low = 0;
        int high = table.size() - 1;
        boolean notfound = true;
        while (notfound) {

            if (low > high) {
                System.out.println("Couldn't find that value from the table.");
                return false;
            }
            int mid = (low + high) / 2;
            int foundValue = table.get(mid).getKey();
            if (foundValue == value) {
                System.out.println("Found the value, removing...");
                table.remove(mid);
                notfound = false;
            } else if (value < foundValue) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }


        setTable(table, hashTable);

        return true;
    }

    /* Pistää kyseiset arvot kyseisille puille*/
    private void setTable(LinkedList<Node> table, String hashTable) {
        if(hashTable.equals("or")) {
            union = table;
        }
        else if(hashTable.equals("and")) {
            intersection = table;
        }
        else if(hashTable.equals("xor")){
            symmetricDifference = table;
        }
    }

    /*writeOutput-metodin apumetodi, joka syöttää jokaisen linkitetyn listan yksi kerrallaan
    * Kirjoitettavaksi. */
    public void writer() {
        writeOutput(union, "or");
        writeOutput(intersection, "and");
        writeOutput(symmetricDifference, "xor");
    }

    /*Tämä metodi tekee ne uudet tiedostot.*/
    private void writeOutput(LinkedList<Node> nodes, String fileName)
    {
        int part1;
        int part2;
        Node node;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName + ".txt"));
            for(int i = 0; i < nodes.size(); i++) {
                node = nodes.get(i);
                part1 = node.getKey();
                if (fileName.equals("or")) {
                    part2 = node.getCount();
                } else if (fileName.equals("and")) {
                    part2 = node.getFirstOccurrence();
                } else
                    part2 = node.getOrigin2();

                String outputrow = part1 + " " + part2;

                bw.write(part1 + " " + part2 + "\n");
            }
            bw.close();
        }
         catch (IOException e) {
            System.err.format("IOException: %s%n", e);
         }
        System.out.println("Writing file " + fileName + ".txt...");
    }

    public Tira() {
        union = new LinkedList<>();
        intersection = new LinkedList<>();
        symmetricDifference = new LinkedList<>();
    }
    public static void main(String[] args)
    {
        System.out.println("Welcome to testing the AVL algorithm. ");
	AVLtester ht=new AVLtester();
        ht.readInput("setA");
        ht.readInput("setB");

        //Täytetään hajautustaulut.

        ht.filler();
        boolean quit = false;

        while(!quit) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("What do you want to do? (5 for commands)");
            try {

                int command = scanner.nextInt();

                switch (command) {
                    case 1:
                        System.out.println("Which value would you like to remove?");
                        int value = scanner.nextInt();
                        //Napataan entteri.
                        scanner.nextLine();
                        System.out.println("And from which table? (options: and, or, xor)");
                        String tableName = scanner.nextLine();
                        //Poistetaan kaikki kyseisen arvon esiintymät.
                        boolean removed = ht.removeFromHash(value, tableName);
                        if(removed)
                            System.out.println("It has been removed.");
                        else
                            System.out.println("Couldnt find it (check name/value)");
                        break;
                    case 2:
                        ht.writer();
                        break;
                    case 3:
                        System.out.println("Number of values: " + ht.tree.getValueCount());
                        System.out.println("Number of nodes: " + ht.tree.getNodeCount());
                        break;
                    case 4:
                        quit = true;
                        break;
                    case 5:
                        ht.listOfCommands();
                        break;
                }
            }catch(InputMismatchException E) {
                System.out.println("Use only keyboard numbers 1, 2, 3 and 4 to operate.");
            }
        }
    }

    private void listOfCommands() {
        System.out.println("|| 1. Remove a value (Alkion poisto)|| 2. Write ||" +
                "\n|| 3. Num of values (Alkioiden lkm) || 4. Quit. ||");

        System.out.println("(use numbers 1-4 to operate.)");
    }


    //Tätä käytettiin apuna originin määrittämisessä. Myöhemmin keksii parempiakin tapoja mut
    //Tällä nyt mennään! Ainakin toimii. 
    private String getOrigin(String tableName) {
        if(tableName.equals("setA")) {
            return "A";
        }
        return "B";
    }
}