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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.cjug.kids.models.CreatePerson;
import org.lgna.story.*;
import org.lgna.story.resources.BipedResource;
// </editor-fold>

class Program extends SProgram {
    
    public static final String PERSON_ID = "personId";
    /* Construct new Program */
    public Program() {
        super();
    }
    /* Create a scene, named myScene */
    private final Scene myScene = new Scene();

    public static void main(String[] args) throws Exception {
//Create a runtime window, then display and activate myScene in the window

        ExecutorService executor = Executors.newFixedThreadPool(10);

        Map<String, Biped> userMap = new HashMap<>();
        final Program story = new Program();
        story.initializeInFrame(args);
        story.setActiveScene(story.getMyScene());

        CamelContext context = new DefaultCamelContext();

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                restConfiguration().component("netty4-http").host("0.0.0.0").port(8283).bindingMode(RestBindingMode.json)
                        .apiContextPath("/api-doc").apiProperty("api.title", "Alice API")
                        .apiProperty("api.version", "1.0.0")
                        // and enable CORS
                        .apiProperty("cors", "true")
                        .enableCORS(true);;

                rest("/games/alice/person")
                        .post().type(CreatePerson.class).outType(CreatePerson.class).to("direct:createPerson")
                        .post("say").type(org.cjug.kids.models.Say.class).to("direct:personSay")
                        .post("jump").to("direct:personJump")
                        .post("turn").type(org.cjug.kids.models.Move.class).to("direct:personTurn")
                        .post("move").type(org.cjug.kids.models.Move.class).to("direct:personMove");

                from("direct:createPerson")
                        .process(new AsyncProcessor() {
                            public boolean process(Exchange exchange, AsyncCallback ac) {

                                CreatePerson createPerson = exchange.getIn().getBody(CreatePerson.class);

                                Map<String, Object> result = new HashMap<String, Object>();
                                String userId = UUID.randomUUID().toString();
                                BipedResource resourceObject = null;
                                try {
                                    Class resource = Class.forName("org.lgna.story.resources.biped." + createPerson.getType() + "Resource" );
                                    Object[] resourceEnums = resource.getEnumConstants();
                                    resourceObject = (BipedResource)resourceEnums[0];
                                } catch (ClassNotFoundException ex) {
                                    Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
                                }  catch (SecurityException ex) {
                                    Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
                                }   catch (IllegalArgumentException ex) {
                                    Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                
                                //Biped childPerson = new ChildPerson(new ChildPersonResource(Gender.MALE, new Color(0.808, 0.58, 0.451), BaseEyeColor.LIGHT_BLUE, MaleChildHairGibs.BLACK, 0.242228, new MaleChildTopAndBottomOutfit(ChildTopPieceShortSleeveCollar.SKYBLUE, MaleChildBottomPieceBigShorts.RED_SWIMTRUNKS), BaseFace.HUMAN_10));

                                Biped childPerson = new Biped(resourceObject);
                                
                                userMap.put(userId, childPerson);

                                result.put("userId", userId);

                                executor.submit(() -> {

                                    childPerson.setPaint(Color.WHITE);
                                    childPerson.setOpacity(1.0);
                                    childPerson.setName("childPerson1");
                                    childPerson.setVehicle(story.myScene);
                                    childPerson.setOrientationRelativeToVehicle(new Orientation(0.0, 0.0, 0.0, 1.0));
                                    childPerson.setPositionRelativeToVehicle(new Position(userMap.size(), 0.0, userMap.size()));
                                    childPerson.setScale(new Scale(1.0, 1.0, 1.0));
                                    //childPerson.say(userId, Say.duration(10));
                                    ac.done(true);
                                });

                                exchange.getIn().setBody(result);
                                return true;
                            }

                            @Override
                            public void process(Exchange exchange) throws Exception {

                            }
                        });

                from("direct:personSay")
                        .process(new AsyncProcessor() {
                            public boolean process(Exchange exchange, AsyncCallback ac) {
                                Biped person = userMap.get(exchange.getIn().getHeader(PERSON_ID));
                                org.cjug.kids.models.Say body = exchange.getIn().getBody(org.cjug.kids.models.Say.class);
                                executor.submit(() -> {
                                    person.say(body.getMessage(), Say.duration(10));
                                    ac.done(true);
                                });
                                return true;
                            }

                            @Override
                            public void process(Exchange exchange) throws Exception {

                            }
                        });
                from("direct:personJump")
                        .process(new AsyncProcessor() {
                            public boolean process(Exchange exchange, AsyncCallback ac) {
                                Biped person = userMap.get(exchange.getIn().getHeader(PERSON_ID));
                                executor.submit(() -> {
                                    person.move(MoveDirection.UP, 0.5, Move.duration(0.25));
                                    person.move(MoveDirection.DOWN, 0.5, Move.duration(0.25));
                                    ac.done(true);
                                });
                                return true;
                            }

                            @Override
                            public void process(Exchange exchange) throws Exception {

                            }
                        });
                from("direct:personTurn")
                        .process(new AsyncProcessor() {
                            public boolean process(Exchange exchange, AsyncCallback ac) {
                                Biped person = userMap.get(exchange.getIn().getHeader(PERSON_ID));
                                org.cjug.kids.models.Move body = exchange.getIn().getBody(org.cjug.kids.models.Move.class);
                                String direction = body.getDirection();
                                executor.submit(() -> {
                                    if (direction.equalsIgnoreCase("left")) {
                                        person.turn(TurnDirection.LEFT, 0.25);
                                    } else if (direction.equalsIgnoreCase("right")) {
                                        person.turn(TurnDirection.RIGHT, 0.25);
                                    }

                                    ac.done(true);
                                });
                                return true;
                            }

                            @Override
                            public void process(Exchange exchange) throws Exception {

                            }
                        });
                from("direct:personMove")
                        .process(new AsyncProcessor() {
                            public boolean process(Exchange exchange, AsyncCallback ac) {
                                Biped person = userMap.get(exchange.getIn().getHeader(PERSON_ID));
                                org.cjug.kids.models.Move body = exchange.getIn().getBody(org.cjug.kids.models.Move.class);
                                String direction = body.getDirection();
                                executor.submit(() -> {
                                    if (direction.equalsIgnoreCase("forward")) {
                                        person.move(MoveDirection.FORWARD, 0.5);
                                    } else if (direction.equalsIgnoreCase("backward")) {
                                        person.move(MoveDirection.BACKWARD, 0.5);
                                    } else if (direction.equalsIgnoreCase("right")) {
                                        person.move(MoveDirection.RIGHT, 0.5);
                                    } else if (direction.equalsIgnoreCase("left")) {
                                        person.move(MoveDirection.LEFT, 0.5);
                                    } else if (direction.equalsIgnoreCase("up")) {
                                        person.move(MoveDirection.UP, 0.5);
                                    } else if (direction.equalsIgnoreCase("down")) {
                                        person.move(MoveDirection.DOWN, 0.5);
                                    }

                                    ac.done(true);
                                });
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
