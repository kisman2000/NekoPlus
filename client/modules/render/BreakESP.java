package me.vaimok.nekoplus.client.modules.render;

import me.vaimok.nekoplus.api.event.events.Render3DEvent;
import me.vaimok.nekoplus.client.modules.Module;
import me.vaimok.nekoplus.client.modules.client.Colors;
import me.vaimok.nekoplus.client.setting.Setting;
import me.vaimok.nekoplus.nekoplus;
import me.vaimok.nekoplus.api.util.moduleUtil.RenderUtil;
import me.vaimok.nekoplus.api.util.moduleUtil.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public
class BreakESP extends Module {
    private final Setting < Integer > boxAlpha = register ( new Setting ( "BoxAlpha" , 85 , 0 , 255 ) );
    private final Setting < Float > lineWidth = register ( new Setting ( "LineWidth" , 1.0f , 0.1f , 5.0f ) );
    private final Timer timer = new Timer ( );
    private final BlockPos lastPos = null;
    public Setting < Boolean > box = register ( new Setting ( "Box" , false ) );
    public Setting < Boolean > outline = register ( new Setting ( "Outline" , true ) );
    public BlockPos currentPos;
    public IBlockState currentBlockState;

    public
    BreakESP ( ) {
        super ( "BreakESP" , "Highlights blocks you mine" , Category.RENDER , true , false , false );
    }

    @Override
    public
    void onTick ( ) {
        if ( currentPos != null ) {
            if ( ! mc.world.getBlockState ( currentPos ).equals ( currentBlockState ) || mc.world.getBlockState ( currentPos ).getBlock ( ) == Blocks.AIR ) {
                currentPos = null;
                currentBlockState = null;
            }
        }
    }

    @Override
    public
    void onRender3D ( Render3DEvent event ) {
        if ( currentPos != null ) {
            Color color = new Color ( 255 , 255 , 255 , 255 );
            Color readyColor = Colors.INSTANCE.isEnabled ( ) ? Colors.INSTANCE.getCurrentColor ( ) : new Color ( 125 , 105 , 255 , 255 );
            RenderUtil.drawBoxESP ( currentPos , timer.passedMs ( (int) ( 2000 * nekoplus.serverManager.getTpsFactor ( ) ) ) ? readyColor : color , false , color , lineWidth.getValue ( ) , outline.getValue ( ) , box.getValue ( ) , boxAlpha.getValue ( ) , false );
            //RenderUtil.(currentPos, (int)timer.getPassedTimeMs(), (int)timer.getPassedTimeMs(), 1, Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), Colors.INSTANCE.getCurrentColor().getBlue());
        }
    }
}
