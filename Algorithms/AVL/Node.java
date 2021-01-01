package rajala.jaakko;

class Node {
    //key on alkio
    //Count on kyseisen alkion määrä
    //firstOccurence kertoo missä kohtaa ensimmäistä kertaa esiintyi.
    //height kertoo nykyisen korkeuden.
    private int key, count, firstOccurrence, height;
    private Node leftChild, rightChild;
    private String origin;
    private boolean removed;

    //Rakentaja
    Node(int key, int firstOccurrence, String origin) {
        this.key = key;
        this.firstOccurrence = firstOccurrence;
        this.origin = origin;
        this.count = 1;
        removed = false;
        leftChild = null;
        rightChild = null;
    }

    //Kertoo onko poistettu vai ei.
    void remove() {
        removed = true;
    }

    //Kertoo onko poistettu vai ei
    boolean notRemoved() {
        return removed;
    }

    /* Kaksi originin hakevaa metodia. Siinä mielessä oli jälkeenpäin ajateltuna turhaa työtä
     * tehdä kaksi, mutta kun olin alkuperäisen getOrigin-metodin ehtinyt jo tehdä ja
     * integroida osaksi muutamaa metodia toisissa luokissa, en enää jaksanut ruveta muuttamaan
     * Sitä sen kummemmin.
     */
    String getOrigin() {
        return origin;
    }

    //Tätä metodia käytetään pelkästään tiedoston kirjoituksessa, kun pitää erotella onko kyseessä
    //A vai B:stä tullut alkio.
    int getOrigin2() {
        if(origin.equals("A")) {
            return 1;
        }
        else
            return 2;
    }

    //Tämä tekee originista AB:n, jos A:sta löytyi jo kyseinen alkio joka myös löytyi Bstä
    void addOrigin(String origin) {
        if(this.origin.equals("A") && origin.equals("B")) {
            this.origin = "AB";
        }
    }

    /* Käytetään, jos löytyi lisää samoja alkioita*/
    void addValueCount() {
        count++;
    }

    // Käytettäisiin, jos poistettaisiin yksittäisiä alkioita (ei periaatteessa käytössä harkkatyössä)
    void removeValueCount() {
        count--;
    }

    //Palauttaa kyseisen alkion esiintymisten summan.
    int getCount() {
        return count;
    }

    //Palauttaa avaimen arvon, eli syötetyn alkion itsensä.
    int getKey() {
        return key;
    }

    //Palauttaa sen kohdan, missä alkio ensimmäisen kerran kohdattiin.
    int getFirstOccurrence() {
        return firstOccurrence;
    }

    //Asettaa vasemman lapsen
    void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    //Asettaa oikean lapsen
    void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    //Hakee vasemman lapsen
    Node getLeftChild() {
        return leftChild;
    }

    //Hakee oikean lapsen
    Node getRightChild() {
        return rightChild;
    }

    //Asettaa sen hetkisen solmun korkeuden.
    void setHeight(int height) {
        this.height = height;
    }

    //Hakee sen korkeuden.
    int getHeight() {
        return height;
    }

    //Seuraavaa ei myöskään tarvittu harjoitustyössä. Kyseessä poistometodia varten
    //Toteutettu metodi, jolla korvattiin tarvittaessa poistettavan solmun avaimen
    //Kaikki arvot, jolloin poistettava itsessään lakkaisi olemasta.
    void valueChanger(int newKey, int newCount, int newOccurrence, String newOrigin) {
        key = newKey;
        count = newCount;
        firstOccurrence = newOccurrence;
        origin = newOrigin;
    }
}
