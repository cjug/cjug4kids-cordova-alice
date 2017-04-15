// <editor-fold defaultstate="collapsed" desc="imports">

import org.lgna.story.*;
import org.lgna.story.resources.sims2.Gender;
import org.lgna.story.resources.sims2.BaseEyeColor;
import org.lgna.story.resources.sims2.ChildTopPieceShortSleeveCollar;
import org.lgna.story.SGround.SurfaceAppearance;
import org.lgna.story.resources.sims2.MaleChildHairGibs;
import org.lgna.story.resources.sims2.ChildPersonResource;
import org.lgna.story.resources.sims2.MaleChildTopAndBottomOutfit;
import org.lgna.story.event.SceneActivationEvent;
import org.lgna.story.resources.sims2.MaleChildBottomPieceBigShorts;
import org.lgna.story.resources.sims2.BaseFace;
// </editor-fold>

class Scene extends SScene {

    /* Construct new Scene */
    public Scene() {
        super();
    }

    /* Event listeners */
    private void initializeEventListeners() {
        this.addSceneActivationListener((SceneActivationEvent event) -> {
            this.myFirstMethod();
        });
    }

    /* Procedures and functions for this scene */
    public void myFirstMethod() {
    }
    /* End procedures and functions for this scene */

    // <editor-fold defaultstate="collapsed" desc="/* Scene fields  */">
    private final SGround ground = new SGround();
    private final SCamera camera = new SCamera();

    // <editor-fold defaultstate="collapsed" desc="/* Scene setup  */">
    private void performCustomSetup() {
//Make adjustments to the starting scene, in a way not available in the Scene editor
    }

    private void performGeneratedSetUp() {
//DO NOT EDIT
//This code is automatically generated.  Any work you perform in this method will be overwritten.
//DO NOT EDIT
        this.setAtmosphereColor(new Color(0.588, 0.886, 0.988));
        this.setFromAboveLightColor(Color.WHITE);
        this.setFromBelowLightColor(Color.BLACK);
        this.setFogDensity(0.0);
        this.setName("myScene");
        this.ground.setPaint(SurfaceAppearance.GRASS);
        this.ground.setOpacity(1.0);
        this.ground.setName("ground");
        this.ground.setVehicle(this);
        this.camera.setName("camera");
        this.camera.setVehicle(this);
        this.camera.setOrientationRelativeToVehicle(new Orientation(0.0, 0.995185, 0.0980144, 6.12323E-17));
        this.camera.setPositionRelativeToVehicle(new Position(9.61E-16, 1.56, -7.85));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="/* Procedures and functions to handle multiple scenes */">
    @Override
    protected void handleActiveChanged(Boolean isActive, Integer activationCount) {
        if (isActive) {
            if (activationCount == 1) {
                this.performGeneratedSetUp();
                this.performCustomSetup();
                this.initializeEventListeners();
            } else {
                this.restoreStateAndEventListeners();
            }
        } else {
            this.preserveStateAndEventListeners();
        }
    }

    public SGround getGround() {
        return this.ground;
    }

    public SCamera getCamera() {
        return this.camera;
    }
    // </editor-fold>
}
