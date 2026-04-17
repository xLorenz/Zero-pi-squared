# Zero-Pi squared

## Keyboard Inputs

Keyboard input and mouse input are handled in the [Panel] class.

They get to the [Player] class through [keyPress(int)] and [keyRelease(int)], and then handled in the [handleInputs()] function wich gets called in the [actionPerformed(ActionEvent)] in the [Panel] class.

The [update(double, int)] function of the [PhysicsBall] class was overriden in the [Player] class. it will handle physics updates and timer updates by the [updateTimers(double)] function.

If the player is falling or has just jumped, the [boolean airBorne] attribute will be set to true. If the collisionListener [LandingListener] is notified of a collision which contact point is below the players hitbox, [boolean airBorne] will be set to false. The player has a coyote time of 0.3s after leaving a surface.

The [draw(Graphics)] function of the [PhysicsBall] class has been overriden by the [Player] class, and will now render a triangle base on the [Vector2 direction] attribute.

[handleInputs()] will resolve any movement of the player, based of attributes [int baseJumpHeight], [int baseSpeed] and [int baseMaxSpeed].

## Mouse Inputs

Added atialiasing to the main [paintComponent] in [Panel] class.

[Player.direction] now follows the mouse cursor through [Mouse Player.mouse.pos], set in the [Panel.mouseMovedOrDragged()].

[Player]'s polygon's points now react to [Player.vel].

## Camera Movement

[Vector2 mapAnchor] of [PhysicsHandler] now responds to [Vector2 mapAnchorVelocity] in [updatePhysics()].

[PhysicsHandler] now accepts a [PhysicsObject mainObject], on which position will update [mapAnchorVelocity] when out of [Boundary boundaries]

## Camera Movement 2

[mapAnchor] in now tied to the origin of the objects and affect chunk calculations. [mapAnchor] and objects position have [Vector2 mapAnchorVelocityScaled] added every loop in [updatePhysics()].

- public double anchorFollowVelocity = 20;
- public double anchorFollowFriction = 0.97;

[drawRecordedChunks(Graphics g)] function added, it will display yellow borders on chunk that have been loaded to the hash map, and fill active chunks green.

## Enemy Structure

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

## First Enemy AI

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

    if (difference != 0 && cos != 0) {
            jumpVelocity = Math.sqrt(Math.abs((handler.gravity * dx * dx) / (2 * cos * cos difference)));
        }

If the condition doesn't meet, the jumpVelocity will be set to [maxJumpStrength], which will also cap it.

[jumpTowardsTarget()] sets [airBorne] to true, which will be set to false once the [class LandingListener extends CollisionListener] gets triggered.

Added a few enemy types as examples:

- Normie: red, 10 health,  20 radius, 45º jump, 500 max jump, 2s jump cd
- Speedster: green, 10 health,  15 radius, 30º jump, 1000 max jump, 0.5s jump cd
- Jumper: blue, 10 health,  25 radius, 70º jump, 1500 max jump, 2.5s jump cd

[class Panel] now allows to accelerate [class PhysicsHandler] [anchorVelocity] inside [class MyKeyAdapter].

## Enemies VFX

[class Enemy] will now only decrease [jumpTimer] if not airborne.

[class Enemy] now overrides [class PhysicsBall] [draw(Graphics g)] giving enemies a "bouncy" look, modifyign the oval's height based on [vel.y]

## Renderer Overhaul

Deleted [class Panel extends JPanel] and implemented [class GameCanvas extends Canvas] as the main renderer.

[setUpGame()] sets up the game ( test for now )
[run()] is the main loop. Inside it Delta Time is calculated along with fps that will now display in the windows title, how cool is that?
[run()] calls:
[update(float dt)] runs the physics and player inputs.
[render()] draws the game using a buffer strategy ( BufferStrategy bs = getBufferStrategy(); ), wrapping everything in do whiles to avoid black frames

## Physics update

Updated physics, eliminating the parameter [airBorne] from all entities and replacing with [supported], inherited from physics objects.

## Particles update

Implemented particles, particle pooling, rendering and updating.
[ParticleUpdater] thread starts in [GameCanvas]'s [addNotify()] and [ParticleHandler.render(Graphics2D g)] runs in [GameCanvas.render()].

Implemented particles for player landing, jumping and sprinting using [SimpleParticle]s.

All particle concrete classes shall be inside [/particles/types].

## Combat implementation infrastructure

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

## Background particles and player damage

Fixed a nullPointerError in [ParticleUpdater] and [ParticleHandler.render()]

Fixed an error in [HealthManager] allowing damage if not vulnerable.

Enemies now damage the player. Damage will emit [TriangleParticle] and player renders with [Player.color.darker()] when not vulnerable.

Implemented the [public boolean background] param for particles, set to false by default.

Implemented [renderBgParticles()] and [renderFgParticles()] in [ParticleHandler].

Added [SimpleBackgroundParticle] to particle types

## Skills implementation

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

## Physics update 2

Updated /physics, adding a physics update thread, unified Batch Rendering and simplified Display actions.

## Area triggers and single press keys

Implemented [AreaCircle] extends [PhysicsBall] and [AreaRect] extends [PhysicsRect]. Both act as area triggers, no collisions, but still detect collisions. Use [getCollisions()] to get a snapshot of the list of collisions.

## PlaceBlock and vfx additions

Added new Vector2.random static method, based around a center, min radius and max radius

Used new Vector2.random in new emitter for SimpleParticle, emitCircle(Vector2 centre, int minRadius, int maxRadius, ...)

Implemented new skill PlaceBlock. It places a chunk aligned block that disappears after 10 seconds.

## Explosions

Added new [ExplosionParticle], instant 20 point polygon, orange by default.

Added new [Explosion], a physics object extending AreaCircle, it emits particles and adds velocity according to its knockback to all colliding objects, then deletes itself.

Added new [Grenade] skills. Left click to shoot out a Nade that will explode once its fuse runs out.

## Explosions damage

Now [damage()] will work across all PhysicsObjects.

Added [damage(int ammount, Vector2 damageOrigin)] for physicsObjects

added [kill(Vector2 origin)] for enemies, coupling with the [emitCircleAway()] method for PhysicParticles.

## Map generator

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

## Skill keys

Modified [Skill] to use a [Key] in the super() method. Added [setTriggerKey(Key triggerKey)] to change skills trigger key.

    protected Skill(Key triggerKey) {
        this.triggerKey = triggerKey;
    }

    public void setTriggerKey(Key triggerKey) {
        this.triggerKey = triggerKey;
    }

## Guns

Added guns to the game!
Simple [Gun] class, similar to skill, shoots [Bullet] with a cooldown based on a triggerKey.

    public void shoot() {
        cooldownTime = coolDown;
        handler.addObject(new Bullet(new Vector2(owner.pos.x, owner.pos.y),
                new Vector2(owner.direction.x, owner.direction.y), bulletDamage, owner));
    }

Must call [updateTimer()] and [handleInputs()].

[Bullet], similar to explosions, self deletes on contact with any PhysicsObject.

## Path Traced Blocks

Placeblock skill now traces a path between the las placed block and the current mouse position while active. This fixes the skill skipping tiles.

Implemented static methods in [Vector2]

    // draws a line of tiles between two index coordinates
    public static ArrayList<Vector2> drawTileLine(
            int x0, int y0,
            int x1, int y1)

:

    // draws a line of tiles between two world coordinates
    public static ArrayList<Vector2> traceRay(
            double x0, double y0,
            double x1, double y1,
            int tileSize)

These set path to ray tracing implementations.

## Screen Shake

Added ScreenShake, a singleton that manages screenshake in a given Display.

    public static void update(double dt) {

        if (time <= 0)
            return;

        double diff = time / maxTime;
        multiplier *= diff; // cuadratic slow down

        display.offsetVel.addLocal(Vector2.random(new Vector2(), 1).scale(multiplier));

    }

- Explosions now create ScreenShake.
- Shooting now create ScreenShake.
- Placing Blocks now create ScreenShake.
- Bullet collisions now create more visible PhysicsParticles.

## World Folliage

Added new NoCollisionBlock class and new Bush extends NoCollisionBlock class.

NoCollisionBlock is similar to AreaRect, it wont collide with objects, but it will detect and retunr collisions.

Bush alters it width and height based on a colliding object velocity, and then bounce back to its original width and height.

Generator will generate bushes on color 0,255,0 ( green ), and for every block, it will check for bushes above and set that blocks [Block folliage] to the block avobe.

[Block folliage] will get damaged at the same time as the block and will have the same hp as the block.

    public void setFolliage(Block folliage) {
        this.folliage = folliage;
        folliage.health = health;
    }

    @Override
    public void damage(double ammount, Vector2 pos) {
        if (folliage != null) {
            folliage.damage(ammount, pos);
        }
        health -= ammount;
        if (health <= 0) {
            health = 0;
            if (handler != null) {
                handler.removeObject(this);
            }

        }
    }

## Bushes VFX

Landing particles are now produced by the Block class.

Bushes emit particles when a moving entity is colliding with it.

    public void emitContactSmokeParticles(PhysicsObject b, Manifold colision) {

        if (colision != null && b instanceof Player) {
            // if (b.vel.lengthSquared() > 200000.0) {
            if (colision.penetration > 1.0) {
                SimpleParticle.emitCircleAway(
                        pos.add(colision.normal.scale(0.5 * height * 0.9)),
                        pos.add(colision.normal.scale(0.5 * height)),
                        1, 2,
                        b.vel.length() / 10,
                        new Color(displayColor.getRed(), displayColor.getGreen(), displayColor.getBlue(), 50),
                        2.5,
                        2.0, (int) (b.vel.length() / 100));
            }
        }
    }

As of now, only the player emits particles with Blocks and Bushes, this uses [instanceof] for type-checking. Looking for a solution to keep OO design.

## Block Shield

Added new Skill [BlockShield]. Generates a ring of block around the player.

    public static List<Vector2> getCircleTiles(int radiusTiles) {
        List<Vector2> result = new ArrayList<>();

        int r2 = radiusTiles * radiusTiles;

        for (int x = -radiusTiles; x <= radiusTiles; x++) {
            for (int y = -radiusTiles; y <= radiusTiles; y++) {
                int dist2 = x * x + y * y;

                if (Math.abs(dist2 - r2) <= radiusTiles) {
                    result.add(new Vector2(x, y));
                }
            }
        }
        return result;
    }

Modified [GRAVITY_TERMINAL_VEL] in [PhysicsObject.java] and modified [addForce] to prevent gravity addition if [vel.y] < GRAVITY_TERMINAL_VEL.

## Object Collisions Reaction ReWrite

Rewrote several Objects collisions using new [onCollisionWithCircle()] and [onCollisionWithRect()] methods. Simplified logic for [Block], [Bush], [Enemy].

Added an outline to Enemies shapes.

Added new [Shotgun] extends [Gun] and [ShotgunBullet] extends [Bullet].

Modified [Gun] constructor to use less parameters, turning them into defined attributes.

## Player Atributes

Added [PlayerAttributes] class. Holds all of the player base attributes and variable attributes.

Added [invulnerabilityTimeMultiplier] to [HealthManager], and renamed variables.

Modified [Player] constructor to no longer require color.

## Enemy damage bug fix

Fixed a bug where Enemies would damage the player even if they didn't collide.

Modified [collideWithRect] and [collideWithCircle] for PhysicsObjects, null checking the manifold before calling [onCollisionWithCircle] and [onCollisionWithRect], so the methods only call if a collision is true.

Modify the CHANGELOG to follow MarkDown formatting properly.

## Ghost Corner Hit Bug Fix

Fixed a bug in [physics.collisions.Collision] where a circle could hit the corner of two adjacent rects, getting incorrect impuse addition.

Now Collision.circleRect() accounts for edge (corner) cases, returning a corner bounce only in a true 45º angle.

## Audio System Implementation

Implemented sound package.

AudioManager is the main class. Works with AudioLoader and AudioAssetCache to carry out main system's funcionalities

Load audio ( clips ) inside [AudioLoader.loadAudioFiles()]

Calls [AudioAssetCache.loadClip()] and [AudioAssetCache.loadBundledIds()].

loadClip() will accept an id (String) and a path (String), loading into AudioAssetCache.audioData ( HashMap ) and AudioAssetCache.format ( Hasmap ) the file's corresponding data.

Then on [AudioManager.playSfx(id)], AudioManager.sfxPlayer (SfxPlayer) calls its play(id) method.

It calls [AudioAssetCache.getClip()]:

    public static Clip getClip(String id) {

        if (bundledIds.containsKey(id)) {
            ArrayList<String> list = new ArrayList<>(bundledIds.get(id));
            id = list.get(rand.nextInt(list.size()));
        }

        byte[] data = audioData.get(id);
        AudioFormat format = formats.get(id);

        if (data == null || format == null) {
            throw new IllegalArgumentException("Clip not loaded: " + id);
        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;

        } catch (LineUnavailableException e) {
            throw new RuntimeException("Failed to create clip: " + id);
        }
    }

Retunrns a new [javax.sound.sampled.Clip] object, assigned to a free SfxChannel.clip. Then channel.applyVolume() and channel.play() are called.

Currently programmed but not implemented pause and resume functionalities (inside AudioManager).

> [!IMPORTANT]
> All sfx files must be .WAV format.
>
> loadBundledIds(String id, List< String > list) <- List of ids, not path.

TODO: Implement MusicPlayer.

> [!TIP]
> [For creating sound variants](https://vocalremover.org/pitch)
>
> [For converting .mp3 to .wav](https://convertio.co/es/)
>
> [For getting cool sfx](https://pixabay.com/)
