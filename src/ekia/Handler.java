package ekia;

import java.awt.Graphics;
import java.util.LinkedList;

public class Handler {

    LinkedList<GameObject> object = new LinkedList<>();

    public void tick() {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.tick();
        }
    }

    public void render(Graphics g) {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.render(g);
        }
    }

    public void addObject(GameObject o) {
        this.object.add(o);
    }

    public void removeObject(GameObject o) {
        this.object.remove(o);
    }

    // Retorna la primera coincidencia del tipo con el objeto en la lista
    public GameObject getObject(ID tipo) {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            if (tempObject.id == tipo) {
                return tempObject;
            }
        }
        return null;
    }

    public void clearEnemies() {

        /*for (int i = 0; i<object.size(); i++){

            GameObject tempObject = object.get(i);
            if (tempObject.getId() == ID.Player){
                object.clear();
                addObject(tempObject);
                break;
            }
        }*/
    }
}
