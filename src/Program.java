// <editor-fold defaultstate="collapsed" desc="imports">

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.converter.AsyncProcessorTypeConverter;
import org.apache.camel.model.rest.RestBindingMode;
import org.lgna.story.*;
import org.lgna.story.resources.sims2.BaseEyeColor;
import org.lgna.story.resources.sims2.BaseFace;
import org.lgna.story.resources.sims2.ChildPersonResource;
import org.lgna.story.resources.sims2.ChildTopPieceShortSleeveCollar;
import org.lgna.story.resources.sims2.Gender;
import org.lgna.story.resources.sims2.MaleChildBottomPieceBigShorts;
import org.lgna.story.resources.sims2.MaleChildHairGibs;
import org.lgna.story.resources.sims2.MaleChildTopAndBottomOutfit;
// </editor-fold>

class Program extends SProgram {

    /* Construct new Program */
    public Program() {
        super();
    }
    /* Create a scene, named myScene */
    private final Scene myScene = new Scene();

    public static void main(String[] args) throws Exception {
//Create a runtime window, then display and activate myScene in the window

        ExecutorService executor = Executors.newFixedThreadPool(10);

        Map<String, ChildPerson> userMap = new HashMap<>();
        final Program story = new Program();
        story.initializeInFrame(args);
        story.setActiveScene(story.getMyScene());
        
        CamelContext context = new DefaultCamelContext();
        
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                
                AsyncProcessor asyncProcessor = AsyncProcessorTypeConverter.convert(new SetPersonProcessor());
                
                restConfiguration().component("netty4-http").port(8282).bindingMode(RestBindingMode.json);
                
                rest("/createPerson").get().to("direct:createPerson");
				
		from("direct:createPerson")
                .process(new AsyncProcessor() {
                    public boolean process(Exchange exchange, AsyncCallback ac) {
                        Map<String, Object> result = new HashMap<String, Object>();
                        String userId = UUID.randomUUID().toString();
                        
                        ChildPerson childPerson = new ChildPerson(new ChildPersonResource(Gender.MALE, new Color(0.808, 0.58, 0.451), BaseEyeColor.LIGHT_BLUE, MaleChildHairGibs.BLACK, 0.242228, new MaleChildTopAndBottomOutfit(ChildTopPieceShortSleeveCollar.SKYBLUE, MaleChildBottomPieceBigShorts.RED_SWIMTRUNKS), BaseFace.HUMAN_10));
                        
                        userMap.put(userId, childPerson);
                        
                        result.put("userId", userId);
                        
                        executor.submit(()->{
                            childPerson.setPaint(Color.WHITE);
                            childPerson.setOpacity(1.0);
                            childPerson.setName("childPerson1");
                            childPerson.setVehicle(story.myScene);
                            childPerson.setOrientationRelativeToVehicle(new Orientation(0.0, 0.0, 0.0, 1.0));
                            childPerson.setPositionRelativeToVehicle(new Position(userMap.size(), 0.0, userMap.size()));
                            childPerson.setScale(new Scale(1.0, 1.0, 1.0));
                            childPerson.say(userId, Say.duration(100));
                            ac.done(true);
                        });
                        
                        exchange.getIn().setBody(result);
                        return true;
                    }
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        
                    }
                });
                
            }
            
        });
        context.start();
    }

    /* End main */


 /* Procedures and functions for this program */
    public Scene getMyScene() {
        return this.myScene;
    }
}
