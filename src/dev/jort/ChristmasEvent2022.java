package dev.jort;

import java.awt.*;
import java.util.Arrays;

import org.dreambot.api.Client;
import org.dreambot.api.data.GameState;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.emotes.Emote;
import org.dreambot.api.methods.emotes.Emotes;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.core.Instance;


/*
Requirements:
no cape equipped
max 10 items in inventory to retrieve rewards
 */

@ScriptManifest(category = Category.MISC, name = "Christmas Event 2022", author = "Jort", version = 1.0,
        description = "Complete Christmas Event 2022.")
public class ChristmasEvent2022 extends AbstractScript implements ChatListener {

    int config = -1;

    int questConfig = 3733; //config=3733 and varbit=14676
    int coalConfig = 14689;
    int myHopper = 46446; // 46448: their hoppers
    int brokenGinger = 46452;
    int delay = 600;

    String guss = "Guss Mistletoad";
    String santa = "Santa Claus";
    Area startArea = new Area(3043, 3425, 3059, 3412);

    String santaScribe = "Santa's scribe";
    String scott = "GreatScott85";

    String gnome = "Gnome inspector";
    String cripple = "MerryMax2000";
    String snowballGuy = "Snowball champion";

    @Override
    public void onStart() {
        Instance.getInstance().setKeyboardInputEnabled(true);
        Instance.getInstance().setMouseInputEnabled(true);
    }

    public Area tileToArea(Tile tile, int radius) {
        return new Area(tile.clone().translate(-radius, -radius), tile.clone().translate(radius, radius));
    }

    public int getAmountOfBread() {
        return Inventory.count(item -> item.getName().contains("ingerbread"));
    }

    public void logout() {
        Tabs.logout();
        Sleep.sleepWhile(() -> Client.getGameState().equals(GameState.LOGGED_IN), 3000);
    }

    public void enableRunIf100() {
        if (Walking.getRunEnergy() < 5) {
            return;
        }
        if (Walking.isRunEnabled()) {
            return;
        }
        Walking.toggleRun();
        Sleep.sleepUntil(Walking::isRunEnabled, 600);
    }

    public boolean walkTo(Area area) {
        if (area.contains(Players.getLocal())) {
            return true;
        }
        if (Walking.getDestinationDistance() > 5.0 && Players.getLocal().isMoving()) {
            return false;
        }
        Walking.walk(area.getCenter());
        return false;
    }

    public boolean startDialogue(String npcName) {
        setState("Starting dialogue with " + npcName);
        if (Dialogues.inDialogue()) {
            return true;
        }
        NPC npc = NPCs.closest(npcName);
        if (npc == null) {
            Logger.log("We cannot find " + npcName + ", waiting before logging out...");
            //logout code should not be needed, gnome is handled already
            Sleep.sleep(4000);
            if (NPCs.closest(npcName) == null) {
                Logger.debug("Logging out as we cannot find " + npcName);
                logout();
            }
            return false;
        }
        npc.interact();
        return Sleep.sleepUntil(Dialogues::inDialogue, 5000);
    }

    public int getAnyOptionIndex(String... options) {
        if (!Dialogues.inDialogue()) {
            return -1;
        }
        for (String option : options) {
            int result = Dialogues.getOptionIndexContaining(option);
            if (result != -1) {
                return result;

            }
        }
        return -1;
    }

    public int getAmountOfCoal() {
        return PlayerSettings.getBitValue(coalConfig);
    }

    public void enterPortalIfOutside() {
        GameObject portal = GameObjects.closest("Christmas portal");
        if (portal != null) {
            setState("Entering portal");
            portal.interact();
        }
        Sleep.sleepWhile(() -> startArea.contains(Players.getLocal()), 5000);
    }


    @Override
    public int onLoop() {

        enableRunIf100();

        //handle dialogue
        if (Dialogues.inDialogue()) {
            if (Dialogues.areOptionsAvailable()) {
                int optionIndex = getAnyOptionIndex("yes", "goodbye", "start", "frostbite", "snowman", "candle", "the ball hit you");
                if (optionIndex == -1) {
                    Logger.error("Cannot find option index in " + Arrays.toString(Dialogues.getOptions()));
                    return delay;
                }
                Dialogues.chooseOption(optionIndex);
            } else {
                setState("Continuing dialogue");
                Keyboard.holdSpace(() -> !Dialogues.canContinue(), 10000);
                //                Dialogues.continueDialogue();
            }
            return delay;
        }
        config = PlayerSettings.getConfig(questConfig);
        if (config >= 4) { //before we need to talk to npcs outside
            enterPortalIfOutside();
        }
        switch (config) {
            case 0: {
                if (!startArea.contains(Players.getLocal())) {
                    setState("Walking to quest area.");
                    walkTo(startArea);
                    break;
                }
                startDialogue(guss);
                break;
            }
            case 1: {
                startDialogue(santa);
                break;
            }
            case 2: {
                startDialogue(guss);
                break;
            }
            case 3: {
                startDialogue(santa);
                break;
            }
            case 4:
            case 68: {
                NPC enemy = NPCs.closest(scott, "LazyLaura94", "JoyfulJudy02", "CharlieChimes06", "Bernard");
                if (enemy == null) {
                    //start game by talking to scribe if enemy not visible
                    startDialogue(santaScribe);
                    break;
                } else {
                    int amountOfCoal = getAmountOfCoal();
                    if (amountOfCoal < 50) {
                        setState("Retrieving coal");
                        GameObject coal = GameObjects.closest("Pile of coal", "Large Pile of Coal");
                        if (coal != null) {
                            coal.interact();
                            Sleep.sleepUntil(() -> getAmountOfCoal() > amountOfCoal, 5000);
                        }
                    } else {
                        //give coal to any npc
                        setState("Giving coal >:)");
                        NPC victim = NPCs.closest(npc -> !npc.getName().equals(santaScribe));
                        if (victim != null) {
                            victim.interact();
                            Sleep.sleepUntil(() -> getAmountOfCoal() < amountOfCoal, 5000);
                        }
                    }
                }
                break;
            }
            case 8197:
            case 8261: {
                startDialogue(santaScribe);
                break;
            }
            case 8198:
            case 8262: {
                //logout if gnome if not visible to teleport there on login + portal entry
                if (NPCs.closest(gnome) == null) {
                    logout();
                    break;
                }
                NPC crawler = NPCs.closest(cripple);
                if (crawler == null) {
                    //start game by talking to gnome if cripple is not visible
                    startDialogue(gnome);
                } else {
                    //27557
                    int amountOfBread = getAmountOfBread();
                    if (amountOfBread < 4) {
                        setState("Taking bread");
                        GameObject bread = GameObjects.closest(gameObject -> gameObject.getName().equals("Gingerbread") && gameObject.getID() != brokenGinger);
                        if (bread != null) {
                            bread.interact();
                            Sleep.sleepUntil(() -> getAmountOfBread() != amountOfBread, 3000);
                        } else {
                            bread = GameObjects.closest(gameObject -> gameObject.getName().equals("Gingerbread")); // game can get stuck if only broken on ground -> no new spawns
                            if (bread != null) {
                                bread.interact();
                                Sleep.sleepUntil(() -> getAmountOfBread() != amountOfBread, 3000);
                            }
                        }

                    } else {
                        setState("Depositing bread");
                        GameObject hopper = GameObjects.closest(myHopper);
                        if (hopper != null) {
                            hopper.interact();
                            Sleep.sleepUntil(() -> getAmountOfBread() == 0, 3000);
                        }
                    }

                }
                break;
            }
            case 2105351:
            case 2105415: {
                startDialogue(gnome);
                break;
            }
            case 2105352:
            case 2105416: {
                startDialogue(snowballGuy);
                break;
            }
            case 2105545:
            case 2105353:
            case 2105481:
            case 2105417: {
                NPC enemy = NPCs.closest("MiniatureMarie", "JacobM7", "Ebenezer1843", "DeathlyFuture3");
                if (enemy == null) {
                    //start game by talking to gus if enemy npc is not visible
                    startDialogue(guss);
                } else {
                    if (Inventory.contains("Snowball", "Golden snowball")) {
                        setState("Throwing snowball");
                        NPC victim = NPCs.closest(npc -> {
                            if (!npc.hasAction("Throw-snowball")) {
                                return false;
                            }
                            if (npc.getName().equals(guss)) {
                                return false;
                            }
                            return true;
                        });
                        if (victim != null) {
                            victim.interact();
                            Sleep.sleepUntil(() -> !Inventory.contains("Snowball", "Golden snowball"), 6000);
                        }
                    } else {
                        setState("Collecting snowball");
                        GameObject ballPile = GameObjects.closest("Golden snowball");
                        if (ballPile == null) {
                            ballPile = GameObjects.closest("Snowball pile");
                        }
                        if (ballPile != null) {
                            ballPile.interact();
                            Sleep.sleepUntil(() -> Inventory.contains("Snowball", "Golden snowball"), 3000);
                        }
                    }
                }

                break;
            }
            case 2105546:
            case 2107594:
            case 2106379:
            case 2106507:
            case 2107595: {
                startDialogue(guss);
                break;
            }
            case 2110476:
            case 2110604:
            case 2111692: {
                Widgets.closeAll();
                setState("Event completed! Stopping script.");
                Logger.log(Color.GREEN, "Event completed! Stopping script.");
                Sleep.sleep(600);
                Emotes.doEmote(Emote.JUMP_FOR_JOY);
                Sleep.sleep(600);
                Tabs.open(Tab.INVENTORY);
                stop();
                break;
            }
            default: {
                setState("Unknown state: " + config);
                break;
            }
        }

        return delay;
    }


    @Override
    public void onPaint(Graphics2D g) {
        this.g = g;
        //setup drawing
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.YELLOW);
        g.setFont(font);

        drawString("JORTs XMAS 2022");
        drawString("Config: " + config);
        drawString(state);

        nextDrawY = topMargin;
    }


    //string drawing formatting variables
    private int fontSize = 17;
    private Font font = new Font("Arial", Font.PLAIN, fontSize);
    private int topMargin = 20;
    private int nextDrawY = topMargin;
    private int leftMargin = 10;
    private int padding = 4;
    private Graphics2D g;
    private String state = "JORTs Christmas 2022";

    public void setState(Object state) {
        if (!this.state.equals(state.toString())) {
            Logger.log("State: " + state);
        }
        this.state = state.toString();
    }

    public void drawString(String string) {
        FontMetrics metrics = g.getFontMetrics(font);
        int height = (int) metrics.getStringBounds(string, g).getHeight();
        int textY = nextDrawY + ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawString(string, leftMargin, textY);
        nextDrawY = nextDrawY + height + padding;
    }

    @Override
    public void onGameMessage(Message message) {
        if (message.getMessage().contains("reach that")) {
            //can happen in coal event, just reset game, but if you wait long enough npcs unstucks itself
            setState("Relogging: game stuck");
            logout();
        }
    }
}
