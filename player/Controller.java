package player;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import physics.Vector2;

public class Controller {

    public Keys keys = new Keys();
    public Mouse mouse = new Mouse();

    public class Key {
        int code;
        boolean pressed = false;

        public Key(int code) {
            this.code = code;
        }
    }

    public class Keys {
        public List<Key> list = new ArrayList<>();
        Key space = new Key(KeyEvent.VK_SPACE);
        Key w = new Key(KeyEvent.VK_W);
        Key a = new Key(KeyEvent.VK_A);
        Key s = new Key(KeyEvent.VK_S);
        Key d = new Key(KeyEvent.VK_D);
        Key q = new Key(KeyEvent.VK_Q);
        Key e = new Key(KeyEvent.VK_E);
        Key x = new Key(KeyEvent.VK_X);
        Key control = new Key(KeyEvent.VK_CONTROL);
        Key shift = new Key(KeyEvent.VK_SHIFT);

        public Keys() {
            this.list.add(space);
            this.list.add(w);
            this.list.add(a);
            this.list.add(s);
            this.list.add(d);
            this.list.add(q);
            this.list.add(e);
            this.list.add(x);
            this.list.add(control);
            this.list.add(shift);
        }

    }

    public class Mouse {
        public Vector2 pos = new Vector2();
        public List<Key> list = new ArrayList<>();
        public Key left = new Key(1);
        public Key middle = new Key(2);
        public Key right = new Key(3);

        Mouse() {
            this.list.add(left);
            this.list.add(middle);
            this.list.add(right);
        }
    }

    public void keyPress(int keyCode) {
        for (Key k : keys.list) {
            if (k.code == keyCode) {
                k.pressed = true;
            }
        }
    }

    public void keyRelease(int keyCode) {
        for (Key k : keys.list) {
            if (k.code == keyCode) {
                k.pressed = false;
            }
        }
    }

    public void mousePress(int mouseCode) {
        for (Key k : mouse.list) {
            if (k.code == mouseCode) {
                k.pressed = true;
            }
        }
    }

    public void mouseRelease(int mouseCode) {
        for (Key k : mouse.list) {
            if (k.code == mouseCode) {
                k.pressed = false;
            }
        }
    }

}
