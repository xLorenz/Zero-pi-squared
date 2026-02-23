package player;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import physics.structures.Vector2;

public class Controller {

    public Keys keys = new Keys();
    public Mouse mouse = new Mouse();

    public class Key {
        public int code;
        public boolean pressed = false;
        public boolean singlePress = false;
        public double timePressed = 0.0;

        public Key(int code) {
            this.code = code;
        }

        public boolean singlePress() {
            if (singlePress) {
                singlePress = false;
                return true;
            } else {
                return false;
            }
        }
    }

    public class Keys {
        public List<Key> list = new ArrayList<>();
        public Key space = new Key(KeyEvent.VK_SPACE);
        public Key w = new Key(KeyEvent.VK_W);
        public Key a = new Key(KeyEvent.VK_A);
        public Key s = new Key(KeyEvent.VK_S);
        public Key d = new Key(KeyEvent.VK_D);
        public Key q = new Key(KeyEvent.VK_Q);
        public Key e = new Key(KeyEvent.VK_E);
        public Key x = new Key(KeyEvent.VK_X);
        public Key control = new Key(KeyEvent.VK_CONTROL);
        public Key shift = new Key(KeyEvent.VK_SHIFT);

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

    public void update(double dt) {
        for (Key k : keys.list) {
            if (k.pressed) {
                k.timePressed += dt;
            }
        }
        for (Key k : mouse.list) {
            if (k.pressed) {
                k.timePressed += dt;
            }
        }
    }

    public void keyPress(int keyCode) {
        for (Key k : keys.list) {
            if (k.code == keyCode) {
                if (!k.pressed) {
                    k.singlePress = true;
                }
                k.pressed = true;
            }
        }
    }

    public void keyRelease(int keyCode) {
        for (Key k : keys.list) {
            if (k.code == keyCode) {
                k.singlePress = false;
                k.pressed = false;
            }
        }
    }

    public void mousePress(int mouseCode) {
        for (Key k : mouse.list) {
            if (k.code == mouseCode) {
                if (!k.pressed) {
                    k.singlePress = true;
                }
                k.pressed = true;
            }
        }
    }

    public void mouseRelease(int mouseCode) {
        for (Key k : mouse.list) {
            if (k.code == mouseCode) {
                k.pressed = false;
                k.singlePress = false;
            }
        }
    }

}
