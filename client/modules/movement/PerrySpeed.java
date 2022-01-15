
package me.vaimok.nekoplus.client.modules.movement;

import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.client.command.Command;
import me.vaimok.nekoplus.api.event.events.ClientEvent;
import me.vaimok.nekoplus.api.event.events.MoveEvent;
import me.vaimok.nekoplus.api.event.events.UpdateWalkingPlayerEvent;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.api.util.moduleUtil.*;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.Random;

public
class PerrySpeed extends Module {
    private static PerrySpeed INSTANCE = new PerrySpeed ( );
    public Setting < Mode > mode = this.register ( new Setting < Mode > ( "Mode" , Mode.INSTANT ) );
    public Setting < Boolean > strafeJump = this.register ( new Setting < Object > ( "Jump" , Boolean.FALSE , v -> this.mode.getValue ( ) == Mode.INSTANT ) );
    public Setting < Boolean > noShake = this.register ( new Setting < Object > ( "NoShake" , Boolean.TRUE , v -> this.mode.getValue ( ) != Mode.INSTANT ) );
    public Setting < Boolean > useTimer = this.register ( new Setting < Object > ( "UseTimer" , Boolean.FALSE , v -> this.mode.getValue ( ) != Mode.INSTANT ) );
    public Setting < Double > zeroSpeed = this.register ( new Setting < Object > ( "0-Speed" , 0.0 , 0.0 , 100.0 , v -> this.mode.getValue ( ) == Mode.VANILLA ) );
    public Setting < Double > speed = this.register ( new Setting < Object > ( "Speed" , 10.0 , 0.1 , 100.0 , v -> this.mode.getValue ( ) == Mode.VANILLA ) );
    public Setting < Double > blocked = this.register ( new Setting < Object > ( "Blocked" , 10.0 , 0.0 , 100.0 , v -> this.mode.getValue ( ) == Mode.VANILLA ) );
    public Setting < Double > unblocked = this.register ( new Setting < Object > ( "Unblocked" , 10.0 , 0.0 , 100.0 , v -> this.mode.getValue ( ) == Mode.VANILLA ) );
    public Setting < Double > yspeed = this.register ( new Setting < Object > ( "YSpeed" , 1.0 , 0.1 , 10.0 , v -> this.mode.getValue ( ) == Mode.Yport ) );
    public double startY = 0.0;
    public boolean antiShake = false;
    public double minY = 0.0;
    public boolean changeY = false;
    private double highChainVal = 0.0;
    private double lowChainVal = 0.0;
    private boolean oneTime = false;
    private double bounceHeight = 0.4;
    private float move = 0.26f;
    private int vanillaCounter = 0;

    public
    PerrySpeed ( ) {
        super ( "PerrySpeed" , "go brrrrrr" , Module.Category.MOVEMENT , true , false , false );
        this.setInstance ( );
    }

    public static
    PerrySpeed getInstance ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new PerrySpeed ( );
        }
        return INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    private
    boolean shouldReturn ( ) {
        return nekoplus.moduleManager.isModuleEnabled ( "Freecam" ) || nekoplus.moduleManager.isModuleEnabled ( "Strafe" );
    }

    @Override
    public
    void onUpdate ( ) {
        if ( this.shouldReturn ( ) || PerrySpeed.mc.player.isSneaking ( ) || PerrySpeed.mc.player.isInWater ( ) || PerrySpeed.mc.player.isInLava ( ) ) {
            return;
        }
        switch (this.mode.getValue ( )) {
            case BOOST: {
                this.doBoost ( );
                break;
            }
            case ACCEL: {
                this.doAccel ( );
                break;
            }
            case Yport: {
                this.handleYPortSpeed ( );
                break;
            }
            case ONGROUND: {
                this.doOnground ( );
                break;
            }
        }
    }

    @SubscribeEvent
    public
    void onUpdateWalkingPlayer ( UpdateWalkingPlayerEvent event ) {
        if ( this.mode.getValue ( ) != Mode.VANILLA || PerrySpeed.nullCheck ( ) ) {
            return;
        }
        switch (event.getStage ( )) {
            case 0: {
                this.vanillaCounter = this.vanilla ( ) ? ++ this.vanillaCounter : 0;
                if ( this.vanillaCounter != 4 ) break;
                this.changeY = true;
                this.minY = PerrySpeed.mc.player.getEntityBoundingBox ( ).minY + ( PerrySpeed.mc.world.getBlockState ( PerrySpeed.mc.player.getPosition ( ) ).getMaterial ( ).blocksMovement ( ) ? - this.blocked.getValue ( ) / 10.0 : this.unblocked.getValue ( ) / 10.0 ) + this.getJumpBoostModifier ( );
                return;
            }
            case 1: {
                if ( this.vanillaCounter == 3 ) {
                    PerrySpeed.mc.player.motionX *= this.zeroSpeed.getValue ( ) / 10.0;
                    PerrySpeed.mc.player.motionZ *= this.zeroSpeed.getValue ( ) / 10.0;
                    break;
                }
                if ( this.vanillaCounter != 4 ) break;
                PerrySpeed.mc.player.motionX /= this.speed.getValue ( ) / 10.0;
                PerrySpeed.mc.player.motionZ /= this.speed.getValue ( ) / 10.0;
                this.vanillaCounter = 2;
            }
        }
    }

    private
    double getJumpBoostModifier ( ) {
        double boost = 0.0;
        if ( PerrySpeed.mc.player.isPotionActive ( MobEffects.JUMP_BOOST ) ) {
            int amplifier = Objects.requireNonNull ( PerrySpeed.mc.player.getActivePotionEffect ( MobEffects.JUMP_BOOST ) ).getAmplifier ( );
            boost *= 1.0 + 0.2 * (double) amplifier;
        }
        return boost;
    }

    private
    boolean vanilla ( ) {
        return PerrySpeed.mc.player.onGround;
    }

    private
    void handleYPortSpeed ( ) {
        if ( ! MotionUtil.isMoving ( PerrySpeed.mc.player ) || PerrySpeed.mc.player.isInWater ( ) && PerrySpeed.mc.player.isInLava ( ) || PerrySpeed.mc.player.collidedHorizontally ) {
            return;
        }
        if ( PerrySpeed.mc.player.onGround ) {
            PerrySpeed.mc.player.jump ( );
            MotionUtil.setSpeed ( PerrySpeed.mc.player , MotionUtil.getBaseMoveSpeed ( ) + this.yspeed.getValue ( ) );
        } else {
            PerrySpeed.mc.player.motionY = - 1.0;
        }
    }

    private
    void doBoost ( ) {
        this.bounceHeight = 0.4;
        this.move = 0.26f;
        if ( PerrySpeed.mc.player.onGround ) {
            this.startY = PerrySpeed.mc.player.posY;
        }
        if ( EntityUtil.getEntitySpeed ( PerrySpeed.mc.player ) <= 1.0 ) {
            this.lowChainVal = 1.0;
            this.highChainVal = 1.0;
        }
        if ( EntityUtil.isEntityMoving ( PerrySpeed.mc.player ) && ! PerrySpeed.mc.player.collidedHorizontally && ! BlockUtil.isBlockAboveEntitySolid ( PerrySpeed.mc.player ) && BlockUtil.isBlockBelowEntitySolid ( PerrySpeed.mc.player ) ) {
            this.oneTime = true;
            this.antiShake = this.noShake.getValue ( ) && PerrySpeed.mc.player.getRidingEntity ( ) == null;
            Random random = new Random ( );
            boolean rnd = random.nextBoolean ( );
            if ( PerrySpeed.mc.player.posY >= this.startY + this.bounceHeight ) {
                PerrySpeed.mc.player.motionY = - this.bounceHeight;
                this.lowChainVal += 1.0;
                if ( this.lowChainVal == 1.0 ) {
                    this.move = 0.075f;
                }
                if ( this.lowChainVal == 2.0 ) {
                    this.move = 0.15f;
                }
                if ( this.lowChainVal == 3.0 ) {
                    this.move = 0.175f;
                }
                if ( this.lowChainVal == 4.0 ) {
                    this.move = 0.2f;
                }
                if ( this.lowChainVal == 5.0 ) {
                    this.move = 0.225f;
                }
                if ( this.lowChainVal == 6.0 ) {
                    this.move = 0.25f;
                }
                if ( this.lowChainVal >= 7.0 ) {
                    this.move = 0.27895f;
                }
                if ( this.useTimer.getValue ( ) ) {
                    nekoplus.timerManager.setTimer ( 1.0f );
                }
            }
            if ( PerrySpeed.mc.player.posY == this.startY ) {
                PerrySpeed.mc.player.motionY = this.bounceHeight;
                this.highChainVal += 1.0;
                if ( this.highChainVal == 1.0 ) {
                    this.move = 0.075f;
                }
                if ( this.highChainVal == 2.0 ) {
                    this.move = 0.175f;
                }
                if ( this.highChainVal == 3.0 ) {
                    this.move = 0.325f;
                }
                if ( this.highChainVal == 4.0 ) {
                    this.move = 0.375f;
                }
                if ( this.highChainVal == 5.0 ) {
                    this.move = 0.4f;
                }
                if ( this.highChainVal >= 6.0 ) {
                    this.move = 0.43395f;
                }
                if ( this.useTimer.getValue ( ) ) {
                    if ( rnd ) {
                        nekoplus.timerManager.setTimer ( 1.3f );
                    } else {
                        nekoplus.timerManager.setTimer ( 1.0f );
                    }
                }
            }
            EntityUtil.moveEntityStrafe ( this.move , PerrySpeed.mc.player );
        } else {
            if ( this.oneTime ) {
                PerrySpeed.mc.player.motionY = - 0.1;
                this.oneTime = false;
            }
            this.highChainVal = 0.0;
            this.lowChainVal = 0.0;
            this.antiShake = false;
            this.speedOff ( );
        }
    }

    private
    void doAccel ( ) {
        this.bounceHeight = 0.4;
        this.move = 0.26f;
        if ( PerrySpeed.mc.player.onGround ) {
            this.startY = PerrySpeed.mc.player.posY;
        }
        if ( EntityUtil.getEntitySpeed ( PerrySpeed.mc.player ) <= 1.0 ) {
            this.lowChainVal = 1.0;
            this.highChainVal = 1.0;
        }
        if ( EntityUtil.isEntityMoving ( PerrySpeed.mc.player ) && ! PerrySpeed.mc.player.collidedHorizontally && ! BlockUtil.isBlockAboveEntitySolid ( PerrySpeed.mc.player ) && BlockUtil.isBlockBelowEntitySolid ( PerrySpeed.mc.player ) ) {
            this.oneTime = true;
            this.antiShake = this.noShake.getValue ( ) && PerrySpeed.mc.player.getRidingEntity ( ) == null;
            Random random = new Random ( );
            boolean rnd = random.nextBoolean ( );
            if ( PerrySpeed.mc.player.posY >= this.startY + this.bounceHeight ) {
                PerrySpeed.mc.player.motionY = - this.bounceHeight;
                this.lowChainVal += 1.0;
                if ( this.lowChainVal == 1.0 ) {
                    this.move = 0.075f;
                }
                if ( this.lowChainVal == 2.0 ) {
                    this.move = 0.175f;
                }
                if ( this.lowChainVal == 3.0 ) {
                    this.move = 0.275f;
                }
                if ( this.lowChainVal == 4.0 ) {
                    this.move = 0.35f;
                }
                if ( this.lowChainVal == 5.0 ) {
                    this.move = 0.375f;
                }
                if ( this.lowChainVal == 6.0 ) {
                    this.move = 0.4f;
                }
                if ( this.lowChainVal == 7.0 ) {
                    this.move = 0.425f;
                }
                if ( this.lowChainVal == 8.0 ) {
                    this.move = 0.45f;
                }
                if ( this.lowChainVal == 9.0 ) {
                    this.move = 0.475f;
                }
                if ( this.lowChainVal == 10.0 ) {
                    this.move = 0.5f;
                }
                if ( this.lowChainVal == 11.0 ) {
                    this.move = 0.5f;
                }
                if ( this.lowChainVal == 12.0 ) {
                    this.move = 0.525f;
                }
                if ( this.lowChainVal == 13.0 ) {
                    this.move = 0.525f;
                }
                if ( this.lowChainVal == 14.0 ) {
                    this.move = 0.535f;
                }
                if ( this.lowChainVal == 15.0 ) {
                    this.move = 0.535f;
                }
                if ( this.lowChainVal == 16.0 ) {
                    this.move = 0.545f;
                }
                if ( this.lowChainVal >= 17.0 ) {
                    this.move = 0.545f;
                }
                if ( this.useTimer.getValue ( ) ) {
                    nekoplus.timerManager.setTimer ( 1.0f );
                }
            }
            if ( PerrySpeed.mc.player.posY == this.startY ) {
                PerrySpeed.mc.player.motionY = this.bounceHeight;
                this.highChainVal += 1.0;
                if ( this.highChainVal == 1.0 ) {
                    this.move = 0.075f;
                }
                if ( this.highChainVal == 2.0 ) {
                    this.move = 0.175f;
                }
                if ( this.highChainVal == 3.0 ) {
                    this.move = 0.375f;
                }
                if ( this.highChainVal == 4.0 ) {
                    this.move = 0.6f;
                }
                if ( this.highChainVal == 5.0 ) {
                    this.move = 0.775f;
                }
                if ( this.highChainVal == 6.0 ) {
                    this.move = 0.825f;
                }
                if ( this.highChainVal == 7.0 ) {
                    this.move = 0.875f;
                }
                if ( this.highChainVal == 8.0 ) {
                    this.move = 0.925f;
                }
                if ( this.highChainVal == 9.0 ) {
                    this.move = 0.975f;
                }
                if ( this.highChainVal == 10.0 ) {
                    this.move = 1.05f;
                }
                if ( this.highChainVal == 11.0 ) {
                    this.move = 1.1f;
                }
                if ( this.highChainVal == 12.0 ) {
                    this.move = 1.1f;
                }
                if ( this.highChainVal == 13.0 ) {
                    this.move = 1.15f;
                }
                if ( this.highChainVal == 14.0 ) {
                    this.move = 1.15f;
                }
                if ( this.highChainVal == 15.0 ) {
                    this.move = 1.175f;
                }
                if ( this.highChainVal == 16.0 ) {
                    this.move = 1.175f;
                }
                if ( this.highChainVal >= 17.0 ) {
                    this.move = 1.175f;
                }
                if ( this.useTimer.getValue ( ) ) {
                    if ( rnd ) {
                        nekoplus.timerManager.setTimer ( 1.3f );
                    } else {
                        nekoplus.timerManager.setTimer ( 1.0f );
                    }
                }
            }
            EntityUtil.moveEntityStrafe ( this.move , PerrySpeed.mc.player );
        } else {
            if ( this.oneTime ) {
                PerrySpeed.mc.player.motionY = - 0.1;
                this.oneTime = false;
            }
            this.antiShake = false;
            this.highChainVal = 0.0;
            this.lowChainVal = 0.0;
            this.speedOff ( );
        }
    }

    private
    void doOnground ( ) {
        this.bounceHeight = 0.4;
        this.move = 0.26f;
        if ( PerrySpeed.mc.player.onGround ) {
            this.startY = PerrySpeed.mc.player.posY;
        }
        if ( EntityUtil.getEntitySpeed ( PerrySpeed.mc.player ) <= 1.0 ) {
            this.lowChainVal = 1.0;
            this.highChainVal = 1.0;
        }
        if ( EntityUtil.isEntityMoving ( PerrySpeed.mc.player ) && ! PerrySpeed.mc.player.collidedHorizontally && ! BlockUtil.isBlockAboveEntitySolid ( PerrySpeed.mc.player ) && BlockUtil.isBlockBelowEntitySolid ( PerrySpeed.mc.player ) ) {
            this.oneTime = true;
            this.antiShake = this.noShake.getValue ( ) && PerrySpeed.mc.player.getRidingEntity ( ) == null;
            Random random = new Random ( );
            boolean rnd = random.nextBoolean ( );
            if ( PerrySpeed.mc.player.posY >= this.startY + this.bounceHeight ) {
                PerrySpeed.mc.player.motionY = - this.bounceHeight;
                this.lowChainVal += 1.0;
                if ( this.lowChainVal == 1.0 ) {
                    this.move = 0.075f;
                }
                if ( this.lowChainVal == 2.0 ) {
                    this.move = 0.175f;
                }
                if ( this.lowChainVal == 3.0 ) {
                    this.move = 0.275f;
                }
                if ( this.lowChainVal == 4.0 ) {
                    this.move = 0.35f;
                }
                if ( this.lowChainVal == 5.0 ) {
                    this.move = 0.375f;
                }
                if ( this.lowChainVal == 6.0 ) {
                    this.move = 0.4f;
                }
                if ( this.lowChainVal == 7.0 ) {
                    this.move = 0.425f;
                }
                if ( this.lowChainVal == 8.0 ) {
                    this.move = 0.45f;
                }
                if ( this.lowChainVal == 9.0 ) {
                    this.move = 0.475f;
                }
                if ( this.lowChainVal == 10.0 ) {
                    this.move = 0.5f;
                }
                if ( this.lowChainVal == 11.0 ) {
                    this.move = 0.5f;
                }
                if ( this.lowChainVal == 12.0 ) {
                    this.move = 0.525f;
                }
                if ( this.lowChainVal == 13.0 ) {
                    this.move = 0.525f;
                }
                if ( this.lowChainVal == 14.0 ) {
                    this.move = 0.535f;
                }
                if ( this.lowChainVal == 15.0 ) {
                    this.move = 0.535f;
                }
                if ( this.lowChainVal == 16.0 ) {
                    this.move = 0.545f;
                }
                if ( this.lowChainVal >= 17.0 ) {
                    this.move = 0.545f;
                }
                if ( this.useTimer.getValue ( ) ) {
                    nekoplus.timerManager.setTimer ( 1.0f );
                }
            }
            if ( PerrySpeed.mc.player.posY == this.startY ) {
                PerrySpeed.mc.player.motionY = this.bounceHeight;
                this.highChainVal += 1.0;
                if ( this.highChainVal == 1.0 ) {
                    this.move = 0.075f;
                }
                if ( this.highChainVal == 2.0 ) {
                    this.move = 0.175f;
                }
                if ( this.highChainVal == 3.0 ) {
                    this.move = 0.375f;
                }
                if ( this.highChainVal == 4.0 ) {
                    this.move = 0.6f;
                }
                if ( this.highChainVal == 5.0 ) {
                    this.move = 0.775f;
                }
                if ( this.highChainVal == 6.0 ) {
                    this.move = 0.825f;
                }
                if ( this.highChainVal == 7.0 ) {
                    this.move = 0.875f;
                }
                if ( this.highChainVal == 8.0 ) {
                    this.move = 0.925f;
                }
                if ( this.highChainVal == 9.0 ) {
                    this.move = 0.975f;
                }
                if ( this.highChainVal == 10.0 ) {
                    this.move = 1.05f;
                }
                if ( this.highChainVal == 11.0 ) {
                    this.move = 1.1f;
                }
                if ( this.highChainVal == 12.0 ) {
                    this.move = 1.1f;
                }
                if ( this.highChainVal == 13.0 ) {
                    this.move = 1.15f;
                }
                if ( this.highChainVal == 14.0 ) {
                    this.move = 1.15f;
                }
                if ( this.highChainVal == 15.0 ) {
                    this.move = 1.175f;
                }
                if ( this.highChainVal == 16.0 ) {
                    this.move = 1.175f;
                }
                if ( this.highChainVal >= 17.0 ) {
                    this.move = 1.2f;
                }
                if ( this.useTimer.getValue ( ) ) {
                    if ( rnd ) {
                        nekoplus.timerManager.setTimer ( 1.3f );
                    } else {
                        nekoplus.timerManager.setTimer ( 1.0f );
                    }
                }
            }
            EntityUtil.moveEntityStrafe ( this.move , PerrySpeed.mc.player );
        } else {
            if ( this.oneTime ) {
                PerrySpeed.mc.player.motionY = - 0.1;
                this.oneTime = false;
            }
            this.antiShake = false;
            this.highChainVal = 0.0;
            this.lowChainVal = 0.0;
            this.speedOff ( );
        }
    }

    @Override
    public
    void onDisable ( ) {
        Command.sendMessage(TextUtil.LIGHT_PURPLE + this.getName() + " off.");
        if ( this.mode.getValue ( ) == Mode.ONGROUND || this.mode.getValue ( ) == Mode.BOOST ) {
            PerrySpeed.mc.player.motionY = - 0.1;
        }
        this.changeY = false;
        nekoplus.timerManager.setTimer ( 1.0f );
        this.highChainVal = 0.0;
        this.lowChainVal = 0.0;
        this.antiShake = false;
    }

    @SubscribeEvent
    public
    void onSettingChange ( ClientEvent event ) {
        if ( event.getStage ( ) == 2 && event.getSetting ( ).equals ( this.mode ) && this.mode.getPlannedValue ( ) == Mode.INSTANT ) {
            PerrySpeed.mc.player.motionY = - 0.1;
        }
    }

    @Override
    public
    String getDisplayInfo ( ) {
        return this.mode.currentEnumName ( );
    }

    @SubscribeEvent
    public
    void onMode ( MoveEvent event ) {
        if ( ! ( this.shouldReturn ( ) || event.getStage ( ) != 0 || this.mode.getValue ( ) != Mode.INSTANT || PerrySpeed.nullCheck ( ) || PerrySpeed.mc.player.isSneaking ( ) || PerrySpeed.mc.player.isInWater ( ) || PerrySpeed.mc.player.isInLava ( ) || PerrySpeed.mc.player.movementInput.moveForward == 0.0f && PerrySpeed.mc.player.movementInput.moveStrafe == 0.0f ) ) {
            if ( PerrySpeed.mc.player.onGround && this.strafeJump.getValue ( ) ) {
                PerrySpeed.mc.player.motionY = 0.4;
                event.setY ( 0.4 );
            }
            MovementInput movementInput = PerrySpeed.mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = PerrySpeed.mc.player.rotationYaw;
            if ( (double) moveForward == 0.0 && (double) moveStrafe == 0.0 ) {
                event.setX ( 0.0 );
                event.setZ ( 0.0 );
            } else {
                if ( (double) moveForward != 0.0 ) {
                    if ( (double) moveStrafe > 0.0 ) {
                        rotationYaw += (float) ( (double) moveForward > 0.0 ? - 45 : 45 );
                    } else if ( (double) moveStrafe < 0.0 ) {
                        rotationYaw += (float) ( (double) moveForward > 0.0 ? 45 : - 45 );
                    }
                    moveStrafe = 0.0f;
                    float f = moveForward == 0.0f ? moveForward : ( moveForward = (double) moveForward > 0.0 ? 1.0f : - 1.0f );
                }
                moveStrafe = moveStrafe == 0.0f ? moveStrafe : ( (double) moveStrafe > 0.0 ? 1.0f : - 1.0f );
                final double cos = Math.cos ( Math.toRadians ( rotationYaw + 90.0f ) );
                final double sin = Math.sin ( Math.toRadians ( rotationYaw + 90.0f ) );
                event.setX ( (double) moveForward * EntityUtil.getMaxSpeed ( ) * cos + (double) moveStrafe * EntityUtil.getMaxSpeed ( ) * sin );
                event.setZ ( (double) moveForward * EntityUtil.getMaxSpeed ( ) * sin - (double) moveStrafe * EntityUtil.getMaxSpeed ( ) * cos );
            }
        }
    }

    private
    void speedOff ( ) {
        float yaw = (float) Math.toRadians ( PerrySpeed.mc.player.rotationYaw );
        if ( BlockUtil.isBlockAboveEntitySolid ( PerrySpeed.mc.player ) ) {
            if ( PerrySpeed.mc.gameSettings.keyBindForward.isKeyDown ( ) && ! PerrySpeed.mc.gameSettings.keyBindSneak.isKeyDown ( ) && PerrySpeed.mc.player.onGround ) {
                PerrySpeed.mc.player.motionX -= (double) MathUtil.sin ( yaw ) * 0.15;
                PerrySpeed.mc.player.motionZ += (double) MathUtil.cos ( yaw ) * 0.15;
            }
        } else if ( PerrySpeed.mc.player.collidedHorizontally ) {
            if ( PerrySpeed.mc.gameSettings.keyBindForward.isKeyDown ( ) && ! PerrySpeed.mc.gameSettings.keyBindSneak.isKeyDown ( ) && PerrySpeed.mc.player.onGround ) {
                PerrySpeed.mc.player.motionX -= (double) MathUtil.sin ( yaw ) * 0.03;
                PerrySpeed.mc.player.motionZ += (double) MathUtil.cos ( yaw ) * 0.03;
            }
        } else if ( ! BlockUtil.isBlockBelowEntitySolid ( PerrySpeed.mc.player ) ) {
            if ( PerrySpeed.mc.gameSettings.keyBindForward.isKeyDown ( ) && ! PerrySpeed.mc.gameSettings.keyBindSneak.isKeyDown ( ) && PerrySpeed.mc.player.onGround ) {
                PerrySpeed.mc.player.motionX -= (double) MathUtil.sin ( yaw ) * 0.03;
                PerrySpeed.mc.player.motionZ += (double) MathUtil.cos ( yaw ) * 0.03;
            }
        } else {
            PerrySpeed.mc.player.motionX = 0.0;
            PerrySpeed.mc.player.motionZ = 0.0;
        }
    }

    public
    enum Mode {
        INSTANT,
        ONGROUND,
        ACCEL,
        BOOST,
        VANILLA,
        Yport
    }
}
