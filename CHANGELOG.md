### Keyboard Inputs
Keyboard input and mouse input are handled in the [Panel] class.

They get to the [Player] class through [keyPress(int)] and [keyRelease(int)], and then handled in the [handleInputs()] function wich gets called in the [actionPerformed(ActionEvent)] in the [Panel] class.

The [update(double, int)] function of the [PhysicsBall] class was overriden in the [Player] class. it will handle physics updates and timer updates by the [updateTimers(double)] function. 

If the player is falling or has just jumped, the [boolean airBorne] attribute will be set to true. If the collisionListener [LandingListener] is notified of a collision which contact point is below the players hitbox, [boolean airBorne] will be set to false. The player has a coyote time of 0.3s after leaving a surface.

The [draw(Graphics)] function of the [PhysicsBall] class has been overriden by the [Player] class, and will now render a triangle base on the [Vector2 direction] attribute.

[handleInputs()] will resolve any movement of the player, based of attributes [int baseJumpHeight], [int baseSpeed] and [int baseMaxSpeed].



### Mouse Inputs
Added atialiasing to the main [paintComponent] in [Panel] class.

[Player.direction] now follows the mouse cursor through [Mouse Player.mouse.pos], set in the [Panel.mouseMovedOrDragged()].

[Player]'s polygon's points now react to [Player.vel].



### Camera Movement
[Vector2 mapAnchor] of [PhysicsHandler] now responds to [Vector2 mapAnchorVelocity] in [updatePhysics()]. 

[PhysicsHandler] now accepts a [PhysicsObject mainObject], on which position will update [mapAnchorVelocity] when out of [Boundary boundaries]



### Camera Movement 2
[mapAnchor] in now tied to the origin of the objects and affect chunk calculations. [mapAnchor] and objects position have [Vector2 mapAnchorVelocityScaled] added every loop in [updatePhysics()]. 

- public double anchorFollowVelocity = 20;
- public double anchorFollowFriction = 0.97;

[drawRecordedChunks(Graphics g)] function added, it will display yellow borders on chunk that have been loaded to the hash map, and fill active chunks green. 



### Enemy Structure

Separated [class Controller] from [Player], the class contains all the keys and mouse info and methods to update them. 

[Player] and [Controller] moved to [/player] directory.

[/enemies] directory contains all enemy code.

[abstract class Enemy] attributes

- public static PhysicsHandler handler;
- public int health;
- public int damage;
- public Vector2 target = new Vector2();
- public Player player;

Methods

- public void pathToPlayer()
- public void jumpTowardsTarget()
- public void damage(int damage)
- public void kill() // rewards and remove()
- public void remove() // remove from Enemy list and handler objects

[class Normie extends Enemy] added.


### First Enemy AI

Implemented new attributes for [abstract class Enemy]

- public boolean airborne;
- public double jumpCooldown;
- public double jumpTimer;
- public int maxJumpStrenght;
- public int jumpAngleDegrees;
- public double jumpAngleRadians;

Implemented new method [public void setAngle(int angleDegrees)] as a constructor helper

[void updateTimers(double dt)] now updates the enemy [jumpTimer], decreasing it to 0 while not being [airBorne]. Called in [void update(double dt)].

Inside [update(double dt)], if [jumpTimer] reaches 0, the enemy will run [jumpTowardsTarget(pathToPlayer())] and set [jumpTimer] to [jumpCooldown].

[public double pathToPlayer()] will return the angle towards the player, based on [jumpAngleRadians], and set [Vector2 target] to the players position. 

[public void jumpTowardsTarget] will run the parabolic throw formula: 

- if (difference != 0 && cos != 0) {
            jumpVelocity = Math.sqrt(Math.abs((handler.gravity * dx * dx) / (2 * cos * cos * difference)));
        }

If the condition doesn't meet, the jumpVelocity will be set to [maxJumpStrength], which will also cap it. 

[jumpTowardsTarget()] sets [airBorne] to true, which will be set to false once the [class LandingListener extends CollisionListener] gets triggered. 

Added a few enemy types as examples:

- Normie: red, 10 health,  20 radius, 45º jump, 500 max jump, 2s jump cd
- Speedster: green, 10 health,  15 radius, 30º jump, 1000 max jump, 0.5s jump cd
- Jumper: blue, 10 health,  25 radius, 70º jump, 1500 max jump, 2.5s jump cd

[class Panel] now allows to accelerate [class PhysicsHandler] [anchorVelocity] inside [class MyKeyAdapter]. 



### Enemies VFX

[class Enemy] will now only decrease [jumpTimer] if not airborne.

[class Enemy] now overrides [class PhysicsBall][draw(Graphics g)] giving enemies a "bouncy" look, modifyign the oval's height based on [vel.y]

### Renderer Overhaul

Deleted [class Panel extends JPanel] and implemented [class GameCanvas extends Canvas] as the main renderer. 

[setUpGame()] sets up the game ( test for now ) 
[run()] is the main loop. Inside it Delta Time is calculated along with fps that will now display in the windows title, how cool is that?
[run()] calls:
[update(float dt)] runs the physics and player inputs.
[render()] draws the game using a buffer strategy ( BufferStrategy bs = getBufferStrategy(); ), wrapping everything in do whiles to avoid black frames

### Physics update
Updated physics, eliminating the parameter [airBorne] from all entities and replacing with [supported], inherited from physics objects.

### Particles update

Implemented particles, particle pooling, rendering and updating.
[ParticleUpdater] thread starts in [GameCanvas]'s [addNotify()] and [ParticleHandler.render(Graphics2D g)] runs in [GameCanvas.render()]. 

Implemented particles for player landing, jumping and sprinting using [SimpleParticle]s.

All particle concrete classes shall be inside [/particles/types].

### Combat implementation infrastructure

Added [HealthManager] to control the players health.

    Methods:
        > void updateTimers(double dt) // updates the invulnerability cd
        >  void damage(int ammount) // deals damage and sets invulnerable
        > void setInvulnerability() // sets invulnerability timer to de default value
        > void setInvulnerability(double time) // sets invulnerability for the input ammount
        > double getPercent() // returns the percentage of health from maxHealth
        > void regenerateHealth(int ammount) // adds ammount to health


Added to [BatchRenderer].
    
    Methods:
        > void drawRect(Vector2 pos, double w, double h)
        > void drawSquare(Vector2 pos, double w)
        > void drawTriangle(Vector2 a, Vector2 b, Vector2 c)
        > void drawPolygon(Vector2[] verts, int count)

These methods allow for batch rendering of different shapes particles.
Added [TriangleParticle], a spinning fading out triangle.
Added [PhysicsParticle], a SimpleParticle that reacts to gravity.

Added to [Vector2].

    Methods:
       > static Vector2 random(int xMin, int xMax, int yMin, int yMax)
       > static Vector2 random(double xMin, double xMax, double yMin, double yMax) 
       > static Vector2 random(Vector2 corner1, Vector2 corner2)
       > static Vector2 random(int[] xBounds, int[] yBounds)

Returns a random vector within bounds

### Background particles and player damage

Fixed a nullPointerError in [ParticleUpdater] and [ParticleHandler.render()]

Fixed an error in [HealthManager] allowing damage if not vulnerable.

Enemies now damage the player. Damage will emit [TriangleParticle] and player renders with [Player.color.darker()] when not vulnerable.

Implemented the [public boolean background] param for particles, set to false by default.

Implemented [renderBgParticles()] and [renderFgParticles()] in [ParticleHandler].

Added [SimpleBackgroundParticle] to particle types

### Skills implementation

Implemented [SkillsManager] for player's skill handling.

    Atributes:
        > private ArrayList<Skill> skills = new ArrayList<>()
        > public Skill selectedSkill
        > private Player player

    Methods:
        > public void updateTimers(double dt) // update timers for all skills
        > public void updateSkills(double dt) // updates skills
        > public void handleInputs(Controller c) // updates activation through inputs
        > public void addSkill(Skill s)
        > public void removeSkill(Skill s)

Implemented abstratc class [Skill].

    Atributes:
        
        > protected double coolDownTime
        > protected double coolDown
        > protected boolean active
        > protected boolean ready

    Methods: 
    
        > public abstract void updateTimer(double dt)
        > public abstract void update(double dt, Player player)
        > public abstract void handleInputs(Controller c)

Implemented concrete Skill classes [Sprint] (replacing sprint mechanic inside Player.handleInputs()) and [DoubleJump].


### Physics update 2

Updated /physics, adding a physics update thread, unified Batch Rendering and simplified Display actions.

### Area triggers and single press keys

Implemented [AreaCircle] extends [PhysicsBall] and [AreaRect] extends [PhysicsRect]. Both act as area triggers, no collisions, but still detect collisions. Use [getCollisions()] to get a snapshot of the list of collisions.

### PlaceBlock and vfx additions

Added new Vector2.random static method, based around a center, min radius and max radius

Used new Vector2.random in new emitter for SimpleParticle, emitCircle(Vector2 centre, int minRadius, int maxRadius, ...)

Implemented new skill PlaceBlock. It places a chunk aligned block that disappears after 10 seconds. 

### Explosions

Added new [ExplosionParticle], instant 20 point polygon, orange by default.

Added new [Explosion], a physics object extending AreaCircle, it emits particles and adds velocity according to its knockback to all colliding objects, then deletes itself.

Added new [Grenade] skills. Left click to shoot out a Nade that will explode once its fuse runs out. 

### Explosions damage

Now [damage()] will work across all PhysicsObjects. 

Added [damage(int ammount, Vector2 damageOrigin)] for physicsObjects

added [kill(Vector2 origin)] for enemies, coupling with the [emitCircleAway()] method for PhysicParticles. 

### Map generator

Added [Generator.java] in /world/terrain.

    Generator.loadMapImage(String filePath) : loads an image for map generation
    Generator.generateMap() : Generates the map, based on the image, placing a chunk-aligned [Block] with the corresponding rgb value. rgb = 0,0,0 is void, ommited, rgb = 255,255,255 is ommited, could be used for feature placing, as well as other specific colors. 

The class [Block] extends PhysicsRect. A chunk-aligned rect, placed based on the x, y chunk coords and a rgb value.  

The image used for the map must not have an excesive ammount of pixels, as each Block is a PhysicsObject and iterates several times in the PhysicsUpdater thread, even if sleeping.

Modified [PhysicsRect.getOccupiedChunks] so that only returns the rect the chunk actually occupies, instead of adding the surrounding ones.

Modified [PhysicsBall.getOccupiedChunks] so that it returns occupied chunks with the surrounding ones. 

These changes optimize looping time, as there will be more rects than circles. 

Added particles to enemies jump, and set them as [forceAwake = true], as sleeping objects don't update.

The [PlaceBlock] skill's rect is now properly chunk-aligned

    Rect rect = new Rect(handler.chunkDimension - 1, handler.chunkDimension - 1, player.color);

Explosions now explode only once, as they could update several times until removed. 

    if (!exploded) {
        explode();
        emitParticles();
        exploded = true;
    }
    handler.removeObject(this);

Implemented a simple pixelart image, to use as a test map, it is really ugly. 


### Skill keys

Modified [Skill] to use a [Key] in the super() method. Added [setTriggerKey(Key triggerKey)] to change skills trigger key.

    protected Skill(Key triggerKey) {
        this.triggerKey = triggerKey;
    }

    public void setTriggerKey(Key triggerKey) {
        this.triggerKey = triggerKey;
    }

### Guns

Added guns to the game!
Simple [Gun] class, similar to skill, shoots [Bullet] with a cooldown based on a triggerKey. 

    public void shoot() {
        cooldownTime = coolDown;
        handler.addObject(new Bullet(new Vector2(owner.pos.x, owner.pos.y),
                new Vector2(owner.direction.x, owner.direction.y), bulletDamage, owner));
    }

Must call [updateTimer()] and [handleInputs()].

[Bullet], similar to explosions, self deletes on contact with any PhysicsObject.
