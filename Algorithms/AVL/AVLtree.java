package rajala.jaakko;

public class AVLtree{
    /*Luokkamuuttujat. */
    private Node root;
    //ValueCount kertoo kuinka monta arvojen ilmentymää kaikkiaan on koko puussa
    // (yhtäsuuret arvot mukaanlukien)
    private int valueCount;
    //nodeCount kertoo kuinka monta solmuja on puussa.
    private int nodeCount;
    private Node[] array;
    private int arrayIndex;

    /* Rakentaja */

    public AVLtree(int key, int firstLocation, String origin) {
        root = new Node(key, firstLocation, origin);
        valueCount = 1;
        nodeCount = 1;

    }

    /* isEmpty() metodi mikä puussa yleensä on. Tässä sitä ei käytetty.*/

    public boolean isEmpty() {
        return root == null;
    }

    /* Seuraavia käytetään aina kun lisätään/poistetaan arvon ilmentymä.*/
    private void valueAdded() {
        valueCount++;
    }

    private void valueRemoved() {
        valueCount--;
    }

    /* Piti keksiä jokin tapa millä saisin näppärästi puun taulukkomuotoon josta
    * Solmuja pystyisi käsittelemään hashejen muodostamista varten. Seuraava osio on
    * Tälle tarkoitettu*/


    public Node[] getAsArray() {
        return array;
    }

    //Käytetään päivittämään taulukon koko.
    public void updateArray() {
        array = new Node[nodeCount];
        arrayIndex = 0;
        //postOrder(root);
    }

    public void addToArray(Node node) {
        array[arrayIndex] = node;
        arrayIndex++;
    }

    public void putNodesToArray(Node node) {

        //System.out.println("This node is: " + node.getKey());
        //System.out.println("Checking Key " + node.getKey() + "s left child");
        if(node.getLeftChild() == null) {
            addToArray(node);
            //System.out.println("Left child is null");
        }
        else {
            putNodesToArray(node.getLeftChild());
            addToArray(node);
        }
        //System.out.println("Checking Key " + node.getKey() + "s right child:");
        if(node.getRightChild() == null) {
            //System.out.println("Right child is null");
        }
        else {
            putNodesToArray(node.getRightChild());
        }
    }


    public int getValueCount() {
        return valueCount;
    }

    public Node getRoot() {
        return root;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    /* Seuraava osio oli tärkeässä asemassa binääripuuhun arvojen lisäämisessä
    * addNode-metodi vastaanottaa syötetyt arvot
    * adder lisää arvot oikeaan kohtaan puussa
    * heightChecker-metodi tarkistaa ettei AVL-puun korkeuserot minkään solmujen välillä
    * Järkkyneet lisäyksen jälkeen
    * balance-checker on jatkumoa height checkerin toimintaan
    * rotator-metodia käytettiin rotaatioiden tekemiseen.
    * Lopputuloksena tasapainotettu puu, joka on samalla myös järjestänyt annetut arvot
    * Helposti saatavaan muotoon myöhempää käsittelyä varteen */
    public void addNode(int keyValue, int location, String origin) {
        root = adder(root, keyValue, location, origin);
        valueAdded();
    }

    private Node adder(Node node, int keyValue, int location, String origin) {
        //Jos kyseinen solmu on null
        if(node == null) {
            //Kyseistä solmua ei ennestään ollut, joten luodaan se.
            //Lisätään nodeCounttiin +1
            nodeCount++;
            //Palautetaan kys arvo
            return new Node(keyValue, location, origin);
        }
        else {
            int currentValue = node.getKey();
            if (keyValue < currentValue) {
                node.setLeftChild(adder(node.getLeftChild(), keyValue, location, origin));
            } else if (keyValue > currentValue) {
                node.setRightChild(adder(node.getRightChild(), keyValue, location, origin));
            }
            //Muuten kyseessä on sama arvo kuin kyseinen solmu.
            else {
                //Lisätään originiin
                node.addOrigin(origin);
                //Ylläpidetään myös kyseisen alkion määrää.
                node.addValueCount();
            }
        }

        //Päivitetään korkeus
        return balanceChecker(node, heightChecker(node));
    }

    private int heightChecker(Node nodes) {
        int leftChildH = (nodes.getLeftChild() == null) ? -1 : nodes.getLeftChild().getHeight();
        int rightChildH = (nodes.getRightChild() == null) ? -1 : nodes.getRightChild().getHeight();


        //POISTA
        //System.out.println(nodes.getKey() + " leftChildH: " + leftChildH);
        //System.out.println(nodes.getKey() + " rightChildH: " + rightChildH);

        int newHeight = 1 + Math.max(leftChildH, rightChildH);
        //Päivittää solmun korkeuden.
        nodes.setHeight(newHeight);

        return rightChildH - leftChildH;
    }

    private Node balanceChecker(Node node, int heightDifference) {
        //Jos korkeusero -1 ja 1 välillä.
        if(-1 <= heightDifference && heightDifference <= 1 ) {
            //System.out.println("No need to rotate");
            return node;
        }

        else {
            String rotationDirection ="";

            //jos korkeusero on suurempi kuin 1, niin oikea lapsi ongelmana
            if (heightDifference > 1) {
                //Jos oikean lapsen oikea lapsi se ongelma:
                if(heightChecker(node.getRightChild()) > 0) {
                    rotationDirection = "left";
                }
                //Muuten solmun oikean lapsen vasen lapsi ongelma:
                else {
                    //Tässä kohtaa menee pikkasen vielä hankalammaksi
                    //Pitää korvata solmun oikea lapsi "ongelma lapsenlapsella"
                    //Tällöin kiertosuunta oikea
                    node.setRightChild(rotator(node.getRightChild(), "right"));
                    //Tämän jälkeen ongelma
                    rotationDirection = "left";
                }
            }

            //jos korkeusero on pienempi kuin -1, niin vasen lapsi ongelmana
            if (heightDifference < -1) {
                if (heightChecker(node.getLeftChild()) < 0) {
                    //Ongelman tuottaa  siis solmun vasemman lapsen vasin lapsi.
                    //Jolloin on tehtävä solmusta kyseisen lapsen
                    //Oikea lapsi jotta ongelma korjaantuu.
                    rotationDirection = "right";
                }
                //Muuten solmun oikean lapsen vasen lapsi ongelma:
                else {
                    //Tässä kohtaa menee pikkasen vielä hankalammaksi
                    //Pitää korvata solmun oikea lapsi "ongelma lapsenlapsella"
                    //Tällöin kiertosuunta oikea
                    node.setLeftChild(rotator(node.getLeftChild(), "left"));
                    rotationDirection = "right";
                }
            }

            return rotator(node, rotationDirection);
        }
    }

    private Node rotator(Node node, String direction) {
        Node newParent = (direction.equals("left") ?
                node.getRightChild() : node.getLeftChild());
        //Jos laskupuoli vasen: Vanhempi "laskeutuu" oikealla olevan lapsensa
        //Vasemmaksi lapseksi.
        if(direction.equals("left")) {
            //Pistetään solmun oikeaksi lapseksi "uuden vanhemman"
            //vasen lapsi.
            node.setRightChild(newParent.getLeftChild());
            //Pistetään "uuden vanhemman" vasemmaksi lapseksi
            //kyseinen solmu.
            newParent.setLeftChild(node);
        }
        //Jos laskupuoli oikea: Vanhempi "laskeutuu" vasemman lapsensa oikeaksi
        //lapseksi
        if(direction.equals("right")) {
            node.setLeftChild(newParent.getRightChild());
            newParent.setRightChild(node);
        }
        //Päivitetään kummankin korkeus: Ensin uuden lapsen, sitten vasta vanhemman
        heightChecker(node);
        heightChecker(newParent);
        return newParent;
    }

    /* HUOM! TÄSTÄ ETEENPÄIN LOPPUOSAA OSAA (remove-smallestChild)EI KÄYTETTY TÄSSÄ Harjoitustyössä!
     * Kyseessä AVLpuun oma solmupoistaja jonka loin tätä työtä varten, mutten lopulta löytänyt
     * Sille sopivaa käyttöä tehtävää varten. En kuitenkaan raaskinut poistaa sitä
     */

    public boolean remove(int keyValue) {
        Node foundNode = removeValue(root, keyValue);
        if(foundNode == null) {
            return false;
        }
        else {
            //Korvataan root tällä muokatulla puulla.
            root = foundNode;
            valueRemoved();
            return true;
        }
    }

    private Node removeValue(Node node, int keyValue) {
        //Ensin pitää löytää solmu uudestaan
        if(node == null) {
            //Ei löytynyt!
            return null;
        }

        int currentKeyValue = node.getKey();

        if(keyValue < currentKeyValue) {
            //Poisto tehdään mahdollisesti vasemmassa lapsessa
            //Jolloin se pitää siis päivittää
            node.setLeftChild(removeValue(node.getLeftChild(), keyValue));
        }
        else if(keyValue > currentKeyValue) {
            //Mahdollinen posto tehdään oikeassa lapsessa
            //Jolloin se pitää siis päivittää
            node.setRightChild(removeValue(node.getRightChild(), keyValue));
        }
        //Muuten ollaan löydetty oikea paikka.
        else {
            //Otetaan kyseisen solmun count, joka kertoo montako esiintymää
            //Kyseisellä avaimella oli laskettu tiedostosta
            int count = node.getCount();

            if(count > 1) {
                //Poistetaan yksi esiintymä
                node.removeValueCount();
                return node;
            }
            //Muuten tiedetään, että esiintymiä on vain yksi,
            //jolloin joudutaan poistamaan Koko solmu laskuista.
            else {
                node = removeNode(node, keyValue);
                nodeCount--;
            }
        }

        return node;
    }

    private Node removeNode(Node node, int keyToRemove) {
        int currentKey = node.getKey();

        if(currentKey == keyToRemove) {
            //Jos poistettavan vasen lapsi on tyhjä, niin poisto tapahtuu
            //Siirtämällä solmun oikea lapsi solmun tilalle.
            if (node.getLeftChild() == null) {
                return node.getRightChild();
            }
            //Jos puolestaan poistettavan oikealapsi on tyhjä, niin
            //Siirretään solmun vasen lapsi solmun tilalle.
            else if (node.getRightChild() == null) {
                return node.getLeftChild();
            }
            //Muussa tapauksessa kumpikaan ei ole tyhjä
            else {
                //Pitää päättää kumpi lapsista korvaa solmun.
                //Otetaan se lapsi, jonka korkeus on tällä hetkellä korkeampi

                //Jos vasen lapsi korkeampi
                if (node.getLeftChild().getHeight() > node.getRightChild().getHeight()) {
                    //Otetaan se solmun vasen lapsi, joka isoin.
                    Node replacerNode = biggestChild(node.getLeftChild());
                    int replacingKey = replacerNode.getKey();
                    int replacingCount = replacerNode.getCount();
                    int replacingOccurence = replacerNode.getFirstOccurrence();
                    String replacingOrigin = replacerNode.getOrigin();

                    node.valueChanger(replacingKey, replacingCount, replacingOccurence, replacingOrigin);

                    //Poistetaan korvaajan esiintymä
                    node.setLeftChild(removeNode(node.getLeftChild(), replacingKey));
                } //Muuten oikea lapsi oletetaan korkeammaksi (vaikka näin ei olisi)
                else {
                    //Otetaan se solmun oikea lapsi, joka pienin.
                    Node replacerNode = smallestChild(node.getRightChild());
                    int replacingKey = replacerNode.getKey();
                    int replacingCount = replacerNode.getCount();
                    int replacingOccurence = replacerNode.getFirstOccurrence();
                    String replacingOrigin = replacerNode.getOrigin();

                    node.valueChanger(replacingKey, replacingCount, replacingOccurence, replacingOrigin);

                    //Poistetaan korvaajan esiintymä
                    node.setRightChild(removeNode(node.getRightChild(), replacingKey));
                }
            }
        }
        else if(keyToRemove < currentKey) {
            //Poisto tehdään mahdollisesti vasemmassa lapsessa
            //Jolloin se pitää siis päivittää
            node.setLeftChild(removeNode(node.getLeftChild(), keyToRemove));
        }
        else {
            //Mahdollinen poisto tehdään oikeassa lapsessa
            //Jolloin se pitää siis päivittää
            node.setRightChild(removeNode(node.getRightChild(), keyToRemove));
        }

        //Taas hoidetaan AVL-balanssi kuosiin.
        return balanceChecker(node, heightChecker(node));
    }

    public Node biggestChild(Node node) {
        if(node.getRightChild() != null)
            biggestChild(node.getRightChild());

        return node;
    }

    public Node smallestChild(Node node) {
        if(node.getLeftChild() != null)
            smallestChild(node.getLeftChild());
        return node;
    }
}