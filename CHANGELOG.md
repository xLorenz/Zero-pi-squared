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

