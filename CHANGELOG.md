- Keyboard Inputs
Keyboard input and mouse input are handled in the [Panel] class.

They get to the [Player] class through [keyPress(int)] and [keyRelease(int)], and then handled in the [handleInputs()] function wich gets called in the [actionPerformed(ActionEvent)] in the [Panel] class.

The [update(double, int)] function of the [PhysicsBall] class was overriden in the [Player] class. it will handle physics updates and timer updates by the [updateTimers(double)] function. 

If the player is falling or has just jumped, the [boolean airBorne] attribute will be set to true. If the collisionListener [LandingListener] is notified of a collision which contact point is below the players hitbox, [boolean airBorne] will be set to false. The player has a coyote time of 0.3s after leaving a surface.

The [draw(Graphics)] function of the [PhysicsBall] class has been overriden by the [Player] class, and will now render a triangle base on the [Vector2 direction] attribute.

[handleInputs()] will resolve any movement of the player, based of attributes [int baseJumpHeight], [int baseSpeed] and [int baseMaxSpeed].



- Mouse Inputs
Added atialiasing to the main [paintComponent] in [Panel] class.

[Player.direction] now follows the mouse cursor through [Mouse Player.mouse.pos], set in the [Panel.mouseMovedOrDragged()].

[Player]'s polygon's points now react to [Player.vel].


- Camera Movement
[Vector2 mapAnchor] of [PhysicsHandler] now responds to [Vector2 mapAnchorVelocity] in [updatePhysics()]. 

[PhysicsHandler] now accepts a [PhysicsObject mainObject], on which position will update [mapAnchorVelocity] when out of [Boundary boundaries]