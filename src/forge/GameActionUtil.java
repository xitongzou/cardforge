package forge;


import forge.card.abilityFactory.AbilityFactory;
import forge.card.cardFactory.CardFactory;
import forge.card.cardFactory.CardFactoryUtil;
import forge.card.spellability.*;
import forge.card.trigger.Trigger;
import forge.gui.GuiUtils;
import forge.gui.input.Input;
import forge.gui.input.Input_PayManaCostUtil;
import forge.gui.input.Input_PayManaCost_Ability;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * <p>GameActionUtil class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class GameActionUtil {
    /**
     * <p>executeUpkeepEffects.</p>
     */
    public static void executeUpkeepEffects() {
        AllZone.getStack().freezeStack();
        upkeep_Braid_Of_Fire();

        upkeep_Slowtrips();  // for "Draw a card at the beginning of the next turn's upkeep."
        upkeep_UpkeepCost(); //sacrifice unless upkeep cost is paid
        upkeep_Echo();

        upkeep_The_Abyss();
        upkeep_Mana_Vortex();
        upkeep_Yawgmoth_Demon();
        upkeep_Lord_of_the_Pit();
        upkeep_Drop_of_Honey();
        upkeep_Demonic_Hordes();
        upkeep_Fallen_Empires_Storage_Lands();
        upkeep_Carnophage();
        upkeep_Sangrophage();
        upkeep_Dega_Sanctuary();
        upkeep_Ceta_Sanctuary();
        upkeep_Tangle_Wire();
        upkeep_Dance_of_the_Dead();
        


        upkeep_Shapeshifter();
        upkeep_Vesuvan_Doppelganger_Keyword();

        //Kinship cards
        upkeep_Ink_Dissolver();
        upkeep_Kithkin_Zephyrnaut();
        upkeep_Leaf_Crowned_Elder();
        upkeep_Mudbutton_Clanger();
        upkeep_Nightshade_Schemers();
        upkeep_Pyroclast_Consul();
        upkeep_Sensation_Gorger();
        upkeep_Squeaking_Pie_Grubfellows();
        upkeep_Wandering_Graybeard();
        upkeep_Waterspout_Weavers();
        upkeep_Winnower_Patrol();
        upkeep_Wolf_Skull_Shaman();

        
        
        upkeep_Sleeper_Agent();
        
        
       // upkeep_Dragon_Broodmother(); //put this before bitterblossom and mycoloth, so that they will resolve FIRST

        //Win / Lose
        // Checks for can't win or can't lose happen in Player.altWinConditionMet()
        
        upkeep_Mortal_Combat();
        upkeep_Near_Death_Experience();
        upkeep_Test_of_Endurance();
        upkeep_Helix_Pinnacle();
        upkeep_Barren_Glory();
        upkeep_Felidar_Sovereign();

        upkeep_Karma();
        upkeep_Oath_of_Druids();
        upkeep_Oath_of_Ghouls();
        upkeep_Suspend();
        upkeep_Vanishing();
        upkeep_Fading();
        upkeep_Masticore();
        upkeep_Eldrazi_Monument();
        upkeep_Blaze_Counters();
        upkeep_Dark_Confidant(); // keep this one semi-last
        upkeep_Power_Surge();
        upkeep_AI_Aluren();
        // experimental, AI abuse aluren

        AllZone.getStack().unfreezeStack();
    }

    /**
     * <p>executeDrawStepEffects.</p>
     */
    public static void executeDrawStepEffects() {
        AllZone.getStack().freezeStack();
        final Player player = AllZone.getPhase().getPlayerTurn();

        draw_Sylvan_Library(player);

        AllZone.getStack().unfreezeStack();
    }

    /**
     * <p>executePlayCardEffects.</p>
     *
     * @param sa a {@link forge.card.spellability.SpellAbility} object.
     */
    public static void executePlayCardEffects(SpellAbility sa) {
        // experimental:
        // this method check for cards that have triggered abilities whenever a
        // card gets played
        // (called in MagicStack.java)
        Card c = sa.getSourceCard();

        playCard_Cascade(c);
        playCard_Ripple(c);
        playCard_Storm(sa);

        playCard_Vengevine(c);
        playCard_Standstill(c);
        playCard_Curse_of_Wizardry(c);
        playCard_Venser_Emblem(c);
        playCard_Ichneumon_Druid(c);

    }

    /**
     * <p>playCard_Cascade.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void playCard_Cascade(final Card c) {
        Command Cascade = new Command() {
            private static final long serialVersionUID = -845154812215847505L;

            public void execute() {

                CardList humanNexus = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer(), "Maelstrom Nexus");
                CardList computerNexus = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer(), "Maelstrom Nexus");

                if (humanNexus.size() > 0) {
                    if (Phase.getPlayerSpellCount() == 1 && !c.isCopiedSpell()) {
                        for (int i = 0; i < humanNexus.size(); i++) {
                            DoCascade(c);
                        }
                    }
                }
                if (computerNexus.size() > 0) {
                    if (Phase.getComputerSpellCount() == 1 && !c.isCopiedSpell()) {
                        for (int i = 0; i < computerNexus.size(); i++) {
                            DoCascade(c);
                        }
                    }
                }
                if (c.hasKeyword("Cascade")
                        || c.getName().equals("Bituminous Blast")) //keyword gets cleared for Bitumonous Blast
                {
                    DoCascade(c);
                }
            }// execute()

            void DoCascade(Card c) {
                final Player controller = c.getController();
                final Card cascCard = c;

                final Ability ability = new Ability(c, "0") {
                    @Override
                    public void resolve() {
                        CardList topOfLibrary = AllZoneUtil.getPlayerCardsInLibrary(controller);
                        CardList revealed = new CardList();

                        if (topOfLibrary.size() == 0) return;

                        Card cascadedCard = null;
                        Card crd;
                        int count = 0;
                        while (cascadedCard == null) {
                            crd = topOfLibrary.get(count++);
                            revealed.add(crd);
                            if ((!crd.isLand() && CardUtil.getConvertedManaCost(crd.getManaCost()) < CardUtil.getConvertedManaCost(cascCard.getManaCost())))
                                cascadedCard = crd;

                            if (count == topOfLibrary.size()) break;

                        }//while
                        GuiUtils.getChoiceOptional("Revealed cards:", revealed.toArray());

                        if (cascadedCard != null && !cascadedCard.isUnCastable()) {

                            if (cascadedCard.getController().isHuman()) {
                                StringBuilder title = new StringBuilder();
                                title.append(cascCard.getName()).append(" - Cascade Ability");
                                StringBuilder question = new StringBuilder();
                                question.append("Cast ").append(cascadedCard.getName()).append(" without paying its mana cost?");

                                int answer = JOptionPane.showConfirmDialog(null, question.toString(), title.toString(), JOptionPane.YES_NO_OPTION);

                                if (answer == JOptionPane.YES_OPTION) {
                                    AllZone.getGameAction().playCardNoCost(cascadedCard);
                                    revealed.remove(cascadedCard);
                                }
                            } else //computer
                            {
                                ArrayList<SpellAbility> choices = cascadedCard.getBasicSpells();

                                for (SpellAbility sa : choices) {
                                    if (sa.canPlayAI()) {
                                        ComputerUtil.playStackFree(sa);
                                        revealed.remove(cascadedCard);
                                        break;
                                    }
                                }
                            }
                        }
                        revealed.shuffle();
                        for (Card bottom : revealed) {
                            AllZone.getGameAction().moveToBottomOfLibrary(bottom);
                        }
                    }
                };
                StringBuilder sb = new StringBuilder();
                sb.append(c).append(" - Cascade.");
                ability.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        };
        Cascade.execute();
    }

    /**
     * <p>playCard_Ripple.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void playCard_Ripple(final Card c) {
        Command Ripple = new Command() {
            private static final long serialVersionUID = -845154812215847505L;

            public void execute() {

                CardList humanThrummingStone = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer(), "Thrumming Stone");
                CardList computerThrummingStone = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer(), "Thrumming Stone");

                for (int i = 0; i < humanThrummingStone.size(); i++) {
                    if (c.getController().isHuman()) c.addExtrinsicKeyword("Ripple:4");
                }
                for (int i = 0; i < computerThrummingStone.size(); i++) {
                    if (c.getController().isComputer()) c.addExtrinsicKeyword("Ripple:4");
                }
                ArrayList<String> a = c.getKeyword();
                for (int x = 0; x < a.size(); x++)
                    if (a.get(x).toString().startsWith("Ripple")) {
                        String parse = c.getKeyword().get(x).toString();
                        String k[] = parse.split(":");
                        DoRipple(c, Integer.valueOf(k[1]));
                    }
            }// execute()

            void DoRipple(Card c, final int RippleCount) {
                final Player controller = c.getController();
                final Card RippleCard = c;
                boolean Activate_Ripple = false;
                if (controller.isHuman()) {
                    Object[] possibleValues = {"Yes", "No"};
                    AllZone.getDisplay().showMessage("Activate Ripple? ");
                    Object q = JOptionPane.showOptionDialog(null, "Activate Ripple for " + c, "Ripple",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                            null, possibleValues, possibleValues[0]);
                    if (q.equals(0)) Activate_Ripple = true;
                } else Activate_Ripple = true;
                if (Activate_Ripple == true) {
                    final Ability ability = new Ability(c, "0") {
                        @Override
                        public void resolve() {
                            CardList topOfLibrary = AllZoneUtil.getPlayerCardsInLibrary(controller);
                            CardList revealed = new CardList();
                            int RippleNumber = RippleCount;
                            if (topOfLibrary.size() == 0) return;
                            int RippleMax = 10; // Shouldn't Have more than Ripple 10, seeing as no cards exist with a ripple greater than 4
                            Card[] RippledCards = new Card[RippleMax];
                            Card crd;
                            if (topOfLibrary.size() < RippleNumber) RippleNumber = topOfLibrary.size();

                            for (int i = 0; i < RippleNumber; i++) {
                                crd = topOfLibrary.get(i);
                                revealed.add(crd);
                                if (crd.getName().equals(RippleCard.getName())) RippledCards[i] = crd;
                            }//For
                            GuiUtils.getChoiceOptional("Revealed cards:", revealed.toArray());
                            for (int i = 0; i < RippleMax; i++) {
                                if (RippledCards[i] != null
                                        && !RippledCards[i].isUnCastable()) {

                                    if (RippledCards[i].getController().isHuman()) {
                                        Object[] possibleValues = {"Yes", "No"};
                                        Object q = JOptionPane.showOptionDialog(null, "Cast " + RippledCards[i].getName() + "?", "Ripple",
                                                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                                null, possibleValues, possibleValues[0]);
                                        if (q.equals(0)) {
                                            AllZone.getGameAction().playCardNoCost(RippledCards[i]);
                                            revealed.remove(RippledCards[i]);
                                        }
                                    } else //computer
                                    {
                                        ArrayList<SpellAbility> choices = RippledCards[i].getBasicSpells();

                                        for (SpellAbility sa : choices) {
                                            if (sa.canPlayAI()
                                                    && !sa.getSourceCard().isType("Legendary")) {
                                                ComputerUtil.playStackFree(sa);
                                                revealed.remove(RippledCards[i]);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            revealed.shuffle();
                            for (Card bottom : revealed) {
                                AllZone.getGameAction().moveToBottomOfLibrary(bottom);
                            }
                        }
                    };
                    StringBuilder sb = new StringBuilder();
                    sb.append(c).append(" - Ripple.");
                    ability.setStackDescription(sb.toString());

                    AllZone.getStack().addSimultaneousStackEntry(ability);

                }
            }
        };
        Ripple.execute();
    }//playCard_Ripple()

    /**
     * <p>playCard_Storm.</p>
     *
     * @param sa a {@link forge.card.spellability.SpellAbility} object.
     */
    public static void playCard_Storm(SpellAbility sa) {
        Card source = sa.getSourceCard();
        if (!source.isCopiedSpell()
                && source.hasKeyword("Storm")) {
            int StormNumber = Phase.getStormCount() - 1;
            for (int i = 0; i < StormNumber; i++)
                AllZone.getCardFactory().copySpellontoStack(source, source, sa, true);
        }
    }//playCard_Storm()

    /**
     * <p>playCard_Vengevine.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void playCard_Vengevine(Card c) {
        if (c.isCreature() == true && (Phase.getPlayerCreatureSpellCount() == 2 || Phase.getComputerCreatureSpellCount() == 2)) {
            final Player controller = c.getController();
            final PlayerZone play = AllZone.getZone(Constant.Zone.Battlefield, controller);
            CardList list = AllZoneUtil.getPlayerGraveyard(controller);
            list = list.getName("Vengevine");
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    final Card card = list.get(i);
                    Ability ability = new Ability(card, "0") {
                        @Override
                        public void resolve() {
                            if (controller.isComputer() || GameActionUtil.showYesNoDialog(card, "Return Vengevine from the graveyard?")) {
                                if (AllZoneUtil.isCardInPlayerGraveyard(controller, card)) {
                                    AllZone.getGameAction().moveTo(play, card);
                                }
                            }
                        }
                    }; // ability

                    StringBuilder sb = new StringBuilder();
                    sb.append(card).append(" - ").append("Whenever you cast a spell, if it's the second creature ");
                    sb.append("spell you cast this turn, you may return Vengevine from your graveyard to the battlefield.");
                    ability.setStackDescription(sb.toString());

                    AllZone.getStack().addSimultaneousStackEntry(ability);

                }
            }//if
        }
    }//playCard_Vengevine()

    /**
     * <p>playCard_Ichneumon_Druid.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void playCard_Ichneumon_Druid(Card c) {
        if (c.isInstant() && (Phase.getPlayerInstantSpellCount() >= 2 || Phase.getComputerInstantSpellCount() >= 2)) {
            final Player player = c.getController();
            final Player opp = player.getOpponent();
            CardList list = AllZoneUtil.getPlayerCardsInPlay(opp, "Ichneumon Druid");
            for (int i = 0; i < list.size(); i++) {
                final Card card = list.get(i);
                Ability ability = new Ability(card, "0") {
                    @Override
                    public void resolve() {
                        player.addDamage(4, card);
                    }
                }; // ability

                StringBuilder sb = new StringBuilder();
                sb.append(card).append(" - ").append("Whenever an opponent casts an instant spell other than the first instant spell that player casts each turn, Ichneumon Druid deals 4 damage to him or her.");
                ability.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(ability);
            }
        }
    }//playCard_Ichneumon_Druid()

    /**
     * <p>playCard_Venser_Emblem.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void playCard_Venser_Emblem(Card c) {
        final Player controller = c.getController();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(controller);

        list = list.filter(new CardListFilter() {
            public boolean addCard(Card crd) {
                return crd.hasKeyword("Whenever you cast a spell, exile target permanent.");
            }
        });

        for (int i = 0; i < list.size(); i++) {
            final Card card = list.get(i);
            final SpellAbility ability = new Ability(card, "0") {
                public void resolve() {
                    Card target = getTargetCard();
                    if (CardFactoryUtil.canTarget(card, target) && AllZoneUtil.isCardInPlay(target))
                        AllZone.getGameAction().exile(target);
                }

                public void chooseTargetAI() {
                    CardList humanList = AllZoneUtil.getPlayerCardsInPlay(AllZone.getHumanPlayer());
                    CardList compList = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer());

                    CardListFilter filter = new CardListFilter() {
                        public boolean addCard(Card c) {
                            return CardFactoryUtil.canTarget(card, c);
                        }
                    };

                    humanList = humanList.filter(filter);
                    compList = compList.filter(filter);

                    if (humanList.size() > 0) {
                        CardListUtil.sortCMC(humanList);
                        setTargetCard(humanList.get(0));
                    } else if (compList.size() > 0) {
                        CardListUtil.sortCMC(compList);
                        compList.reverse();
                        setTargetCard(compList.get(0));
                    }

                }
            };

            Input runtime = new Input() {
                private static final long serialVersionUID = -7620283169787412409L;

                @Override
                public void showMessage() {
                    CardList list = AllZoneUtil.getCardsInPlay();
                    list = list.filter(new CardListFilter() {
                        public boolean addCard(Card c) {
                            return c.isPermanent() && CardFactoryUtil.canTarget(card, c);
                        }
                    });

                    stopSetNext(CardFactoryUtil.input_targetSpecific(ability, list,
                            "Select target permanent to Exile", true, false));
                }//showMessage()
            };//Input

            ability.setBeforePayMana(runtime);
            if (controller.isHuman())
                AllZone.getGameAction().playSpellAbility(ability);
            else {
                ability.chooseTargetAI();

                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }
    }


    /**
     * <p>playCard_Standstill.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void playCard_Standstill(Card c) {
        CardList list = AllZoneUtil.getCardsInPlay("Standstill");

        for (int i = 0; i < list.size(); i++) {
            final Player drawer = c.getController().getOpponent();
            final Card card = list.get(i);

            Ability ability2 = new Ability(card, "0") {
                @Override
                public void resolve() {
                    // sac standstill
                    AllZone.getGameAction().sacrifice(card);
                    // player who didn't play spell, draws 3 cards
                    drawer.drawCards(3);
                }
            }; // ability2

            StringBuilder sb = new StringBuilder();
            sb.append(card.getName()).append(" - ").append(c.getController());
            sb.append(" played a spell, ").append(drawer).append(" draws three cards.");
            ability2.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability2);

        }

    }


    /**
     * <p>playCard_Curse_of_Wizardry.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void playCard_Curse_of_Wizardry(final Card c) {
        CardList list = AllZoneUtil.getCardsInPlay("Curse of Wizardry");

        if (list.size() > 0) {
            ArrayList<String> cl = CardUtil.getColors(c);

            for (int i = 0; i < list.size(); i++) {
                final Card card = list.get(i);
                if (cl.contains(card.getChosenColor())) {
                    Ability ability = new Ability(card, "0") {
                        public void resolve() {
                            c.getController().loseLife(1, card);
                        } //resolve
                    };//ability

                    StringBuilder sb = new StringBuilder();
                    sb.append(card.getName()).append(" - ").append(c.getController());
                    sb.append(" played a ").append(card.getChosenColor()).append(" spell, ");
                    sb.append(c.getController()).append(" loses 1 life.");
                    ability.setStackDescription(sb.toString());

                    AllZone.getStack().addSimultaneousStackEntry(ability);

                }
            }//if
        }//if
    }//Curse of Wizardry

    //UPKEEP CARDS:

    /**
     * <p>payManaDuringAbilityResolve.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param manaCost a {@link java.lang.String} object.
     * @param paid a {@link forge.Command} object.
     * @param unpaid a {@link forge.Command} object.
     */
    public static void payManaDuringAbilityResolve(String message, String manaCost, Command paid, Command unpaid) {
        // temporarily disable the Resolve flag, so the user can payMana for the resolving Ability
        boolean bResolving = AllZone.getStack().getResolving();
        AllZone.getStack().setResolving(false);
        AllZone.getInputControl().setInput(new Input_PayManaCost_Ability(message, manaCost, paid, unpaid));
        AllZone.getStack().setResolving(bResolving);
    }

    /**
     * <p>upkeep_Braid_Of_Fire.</p>
     */
    private static void upkeep_Braid_Of_Fire() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList braids = AllZoneUtil.getPlayerCardsInPlay(player, "Braid of Fire");

        for (int i = 0; i < braids.size(); i++) {
            final Card c = braids.get(i);

            final StringBuilder sb = new StringBuilder();
            sb.append("Cumulative Upkeep for ").append(c).append("\n");
            final Ability upkeepAbility = new Ability(c, "0") {
                @Override
                public void resolve() {
                    c.addCounter(Counters.AGE, 1);
                    int ageCounters = c.getCounters(Counters.AGE);
                    Ability_Mana abMana = new Ability_Mana(c, "0", "R", ageCounters) {
                        private static final long serialVersionUID = -2182129023960978132L;
                    };
                    if (player.isComputer()) {
                        abMana.produceMana();
                    } else if (GameActionUtil.showYesNoDialog(c, sb.toString())) {
                        abMana.produceMana();
                    } else {
                        AllZone.getGameAction().sacrifice(c);
                    }

                }
            };
            upkeepAbility.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(upkeepAbility);

        }
    } //upkeep_Braid_of_Fire

    /**
     * <p>upkeep_Echo.</p>
     */
    private static void upkeep_Echo() {
        CardList list = AllZoneUtil.getPlayerCardsInPlay(AllZone.getPhase().getPlayerTurn());
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.hasKeyword("(Echo unpaid)");
            }
        });

        for (int i = 0; i < list.size(); i++) {
            final Card c = list.get(i);
            if (c.getIntrinsicKeyword().contains("(Echo unpaid)")) {

                final Command paidCommand = Command.Blank;

                final Command unpaidCommand = new Command() {
                    private static final long serialVersionUID = -7354791599039157375L;

                    public void execute() {
                        AllZone.getGameAction().sacrifice(c);
                    }
                };

                final Ability aiPaid = upkeepAIPayment(c, c.getEchoCost());

                final StringBuilder sb = new StringBuilder();
                sb.append("Echo for ").append(c).append("\n");

                final Ability sacAbility = new Ability(c, "0") {
                    @Override
                    public void resolve() {
                        if (c.getController().isHuman()) {
                            payManaDuringAbilityResolve(sb.toString(), c.getEchoCost(), paidCommand, unpaidCommand);
                        } else //computer
                        {
                            if (ComputerUtil.canPayCost(aiPaid))
                                ComputerUtil.playNoStack(aiPaid);
                            else
                                AllZone.getGameAction().sacrifice(c);
                        }
                    }
                };
                sacAbility.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(sacAbility);


                c.removeIntrinsicKeyword("(Echo unpaid)");
            }
        }
    }//echo

    /**
     * <p>upkeep_Slowtrips.</p>
     */
    private static void upkeep_Slowtrips() {  // Draw a card at the beginning of the next turn's upkeep.
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = player.getSlowtripList();

        for (int i = 0; i < list.size(); i++) {
            Card card = list.get(i);
            card.removeIntrinsicKeyword("Draw a card at the beginning of the next turn's upkeep."); //otherwise another slowtrip gets added

            final Ability slowtrip = new Ability(card, "0") {
                @Override
                public void resolve() {
                    player.drawCard();
                }
            };
            slowtrip.setStackDescription(card.getName() + " - Draw a card");

            AllZone.getStack().addSimultaneousStackEntry(slowtrip);


        }
        player.clearSlowtripList();

        //Do the same for the opponent
        final Player opponent = player.getOpponent();

        list = opponent.getSlowtripList();

        for (int i = 0; i < list.size(); i++) {
            Card card = list.get(i);
            card.removeIntrinsicKeyword("Draw a card at the beginning of the next turn's upkeep."); //otherwise another slowtrip gets added

            final Ability slowtrip = new Ability(card, "0") {
                @Override
                public void resolve() {
                    opponent.drawCard();
                }
            };
            slowtrip.setStackDescription(card.getName() + " - Draw a card");

            AllZone.getStack().addSimultaneousStackEntry(slowtrip);

        }
        opponent.clearSlowtripList();
    }

    /**
     * <p>upkeep_UpkeepCost.</p>
     */
    private static void upkeep_UpkeepCost() {
        CardList list = AllZoneUtil.getPlayerCardsInPlay(AllZone.getPhase().getPlayerTurn());

        for (int i = 0; i < list.size(); i++) {
            final Card c = list.get(i);
            final Player controller = c.getController();
            ArrayList<String> a = c.getKeyword();
            for (int j = 0; j < a.size(); j++) {
                String ability = a.get(j);

                //destroy
                if (ability.startsWith("At the beginning of your upkeep, destroy CARDNAME")) {
                    String k[] = ability.split(" pay ");
                    final String upkeepCost = k[1].toString();


                    final Command unpaidCommand = new Command() {
                        private static final long serialVersionUID = 8942537892273123542L;

                        public void execute() {
                            if (c.getName().equals("Cosmic Horror")) {
                            	controller.addDamage(7, c);
                            }
                            AllZone.getGameAction().destroy(c);
                        }
                    };

                    final Command paidCommand = Command.Blank;

                    final Ability aiPaid = upkeepAIPayment(c, upkeepCost);

                    final StringBuilder sb = new StringBuilder();
                    sb.append("Upkeep for ").append(c).append("\n");
                    final Ability upkeepAbility = new Ability(c, "0") {
                        @Override
                        public void resolve() {
                            if (controller.isHuman()) {
                                payManaDuringAbilityResolve(sb.toString(), upkeepCost, paidCommand, unpaidCommand);
                            } else //computer
                            {
                                if (ComputerUtil.canPayCost(aiPaid)
                                        && !c.hasKeyword("Indestructible"))
                                    ComputerUtil.playNoStack(aiPaid);
                                else
                                    AllZone.getGameAction().destroy(c);
                            }
                        }
                    };
                    upkeepAbility.setStackDescription(sb.toString());
                    upkeepAbility.setDescription(sb.toString());

                    AllZone.getStack().addSimultaneousStackEntry(upkeepAbility);
                }//destroy
                
                //sacrifice
                if (ability.startsWith("At the beginning of your upkeep, sacrifice") || ability.startsWith("Cumulative upkeep")) {
                	String cost = "0";
                    final StringBuilder sb = new StringBuilder();
                	
                	if (ability.startsWith("At the beginning of your upkeep, sacrifice")) {
	                    String k[] = ability.split(" pay ");
	                    cost = k[1].toString();
	                    sb.append("Sacrifice upkeep for ").append(c).append("\n");
                	}
                	
                	if (ability.startsWith("Cumulative upkeep")) {
	                    String k[] = ability.split(":");
	                    c.addCounter(Counters.AGE, 1);
	                    cost = CardFactoryUtil.multiplyManaCost(k[1], c.getCounters(Counters.AGE));
	                    sb.append("Cumulative upkeep for ").append(c).append("\n");
                	}
                	
                	final String upkeepCost = cost;

                    final Command unpaidCommand = new Command() {
                        private static final long serialVersionUID = 5612348769167529102L;

                        public void execute() {
                            AllZone.getGameAction().sacrifice(c);
                        }
                    };

                    final Command paidCommand = Command.Blank;

                    final Ability aiPaid = upkeepAIPayment(c, upkeepCost);

                    final Ability upkeepAbility = new Ability(c, "0") {
                        @Override
                        public void resolve() {
                            if (controller.isHuman()) {
                                payManaDuringAbilityResolve(sb.toString(), upkeepCost, paidCommand, unpaidCommand);
                            } else //computer
                            {
                                if (ComputerUtil.canPayCost(aiPaid))
                                    ComputerUtil.playNoStack(aiPaid);
                                else AllZone.getGameAction().sacrifice(c);
                            }
                        }
                    };
                    upkeepAbility.setStackDescription(sb.toString());
                    upkeepAbility.setDescription(sb.toString());

                    AllZone.getStack().addSimultaneousStackEntry(upkeepAbility);
                }//sacrifice
                
                //destroy
                if (ability.startsWith("At the beginning of your upkeep, CARDNAME deals ")) {
                    String k[] = ability.split("deals ");
                    String s1 = k[1].substring(0, 2);
                    final int upkeepDamage =  Integer.parseInt(s1.trim());
                    String l[] = k[1].split(" pay ");
                    final String upkeepCost = l[1].toString();

                    final Command unpaidCommand = new Command() {
                        private static final long serialVersionUID = 1238166187561501928L;

                        public void execute() {
                        	controller.addDamage(upkeepDamage, c);
                        }
                    };

                    final Command paidCommand = Command.Blank;

                    final Ability aiPaid = upkeepAIPayment(c, upkeepCost);

                    final StringBuilder sb = new StringBuilder();
                    sb.append("Damage upkeep for ").append(c).append("\n");
                    final Ability upkeepAbility = new Ability(c, "0") {
                        @Override
                        public void resolve() {
                            if (controller.isHuman()) {
                                payManaDuringAbilityResolve(sb.toString(), upkeepCost, paidCommand, unpaidCommand);
                            } else //computer
                            {
                                if (ComputerUtil.canPayCost(aiPaid)
                                        && controller.predictDamage(upkeepDamage, c, false) > 0)
                                    ComputerUtil.playNoStack(aiPaid);
                                else
                                	controller.addDamage(upkeepDamage, c);
                            }
                        }
                    };
                    upkeepAbility.setStackDescription(sb.toString());
                    upkeepAbility.setDescription(sb.toString());

                    AllZone.getStack().addSimultaneousStackEntry(upkeepAbility);
                }//destroy
            }

        }//for
    }//upkeepCost


    /**
     * <p>upkeep_DamageUpkeepCost.</p>
     */
    /*private static void upkeep_DamageUpkeepCost() {
        CardList list = AllZoneUtil.getPlayerCardsInPlay(AllZone.getPhase().getPlayerTurn());
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                ArrayList<String> a = c.getKeyword();
                for (int i = 0; i < a.size(); i++) {
                    if (a.get(i).toString().startsWith(
                            "At the beginning of your upkeep, CARDNAME deals ")) {
                        String k[] = a.get(i).toString().split("deals ");
                        String s1 = k[1].substring(0, 2);
                        s1 = s1.trim();
                        c.setUpkeepDamage(Integer.parseInt(s1));
                        System.out.println(k[1]);
                        String l[] = k[1].split(" pay ");
                        System.out.println(l[1]);
                        c.setUpkeepCost(l[1]);

                        return true;
                    }
                }
                return false;
            }
        });

        for (int i = 0; i < list.size(); i++) {
            final Card c = list.get(i);

            final Command unpaidCommand = new Command() {
                private static final long serialVersionUID = 8942537892273123542L;

                public void execute() {
                    Player player = c.getController();
                    player.addDamage(c.getUpkeepDamage(), c);
                }
            };

            final Command paidCommand = Command.Blank;

            final Ability aiPaid = upkeepAIPayment(c, c.getUpkeepCost());

            final StringBuilder sb = new StringBuilder();
            sb.append("Upkeep for ").append(c).append("\n");
            final Ability upkeepAbility = new Ability(c, c.getUpkeepCost()) {
                @Override
                public void resolve() {
                    if (c.getController().isHuman()) {
                        payManaDuringAbilityResolve(sb.toString(), c.getUpkeepCost(), paidCommand, unpaidCommand);

                    } else //computer
                    {
                        if (ComputerUtil.canPayCost(aiPaid)) ComputerUtil.playNoStack(aiPaid);
                        else AllZone.getGameAction().sacrifice(c);
                    }
                }
            };
            upkeepAbility.setStackDescription(sb.toString());
            upkeepAbility.setDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(upkeepAbility);

        }
    }//damageUpkeepCost
     */
    /**
     * <p>upkeepAIPayment.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param cost a {@link java.lang.String} object.
     * @param cost a {@link java.lang.String} object.
     * @return a {@link forge.card.spellability.Ability} object.
     */
    private static Ability upkeepAIPayment(Card c, String cost) {
        return new Ability_Static(c, cost) {
            @Override
            public void resolve() {

            }
        };
    }

    /**
     * <p>upkeep_The_Abyss.</p>
     */
    private static void upkeep_The_Abyss() {
        /*
		 * At the beginning of each player's upkeep, destroy target
		 * nonartifact creature that player controls of his or her
		 * choice. It can't be regenerated.
		 */
        final Player player = AllZone.getPhase().getPlayerTurn();
        final CardList the = AllZoneUtil.getCardsInPlay("The Abyss");
        final CardList magus = AllZoneUtil.getCardsInPlay("Magus of the Abyss");

        CardList cards = new CardList();
        cards.addAll(the);
        cards.addAll(magus);

        for (Card c : cards) {
            final Card abyss = c;

            final Ability sacrificeCreature = new Ability(abyss, "") {
                @Override
                public void resolve() {
                    if (player.isHuman()) {
                        if (abyss_getTargets(player, abyss).size() > 0) {
                            AllZone.getInputControl().setInput(new Input() {
                                private static final long serialVersionUID = 4820011040853968644L;

                                public void showMessage() {
                                    AllZone.getDisplay().showMessage(abyss.getName() + " - Select one nonartifact creature to destroy");
                                    ButtonUtil.disableAll();
                                }

                                public void selectCard(Card selected, PlayerZone zone) {
                                    //probably need to restrict by controller also
                                    if (selected.isCreature() && !selected.isArtifact() && CardFactoryUtil.canTarget(abyss, selected)
                                            && zone.is(Constant.Zone.Battlefield) && zone.getPlayer().isHuman()) {
                                        AllZone.getGameAction().destroyNoRegeneration(selected);
                                        stop();
                                    }
                                }//selectCard()
                            });//Input
                        }
                    } else { //computer
                        CardList targets = abyss_getTargets(player, abyss);
                        CardList indestruct = targets.getKeyword("Indestructible");
                        if (indestruct.size() > 0) {
                            AllZone.getGameAction().destroyNoRegeneration(indestruct.get(0));
                        } else {
                            Card target = CardFactoryUtil.AI_getWorstCreature(targets);
                            if (null == target) {
                                //must be nothing valid to destroy
                            } else AllZone.getGameAction().destroyNoRegeneration(target);
                        }
                    }
                }//resolve
            };//sacrificeCreature

            StringBuilder sb = new StringBuilder();
            sb.append(abyss.getName()).append(" - destroy a nonartifact creature of your choice.");
            sacrificeCreature.setStackDescription(sb.toString());

            if (abyss_getTargets(player, abyss).size() > 0)
                AllZone.getStack().addSimultaneousStackEntry(sacrificeCreature);

        }//end for
    }//The Abyss

    /**
     * <p>abyss_getTargets.</p>
     *
     * @param player a {@link forge.Player} object.
     * @param card a {@link forge.Card} object.
     * @return a {@link forge.CardList} object.
     */
    private static CardList abyss_getTargets(final Player player, Card card) {
        CardList creats = AllZoneUtil.getCreaturesInPlay(player);
        creats = creats.filter(AllZoneUtil.nonartifacts);
        creats = creats.getTargetableCards(card);
        return creats;
    }

    /**
     * <p>upkeep_Mana_Vortex.</p>
     */
    private static void upkeep_Mana_Vortex() {
        /*
		 * At the beginning of each player's upkeep, that player
		 * sacrifices a land.
		 */
        final Player player = AllZone.getPhase().getPlayerTurn();
        final CardList vortices = AllZoneUtil.getCardsInPlay("Mana Vortex");

        for (Card c : vortices) {
            final Card vortex = c;

            final Ability sacrificeLand = new Ability(vortex, "") {
                @Override
                public void resolve() {
                    CardList choices = AllZoneUtil.getPlayerLandsInPlay(player);
                    player.sacrificePermanent(vortex.getName() + " - select a land to sacrifice.", choices);

                    //if no lands in play, sacrifice all "Mana Vortex"s
                    if (AllZoneUtil.getLandsInPlay().size() == 0) {
                        for (Card d : vortices) {
                            AllZone.getGameAction().sacrifice(d);
                        }
                        return;
                    }
                }//resolve
            };//sacrificeCreature

            StringBuilder sb = new StringBuilder();
            sb.append(vortex.getName()).append(" - " + player + " sacrifices a land.");
            sacrificeLand.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(sacrificeLand);

        }//end for
    }//Mana_Vortex


    /**
     * <p>upkeep_Yawgmoth_Demon.</p>
     */
    private static void upkeep_Yawgmoth_Demon() {
        /*
		 * At the beginning of your upkeep, you may sacrifice an artifact. If
		 * you don't, tap Yawgmoth Demon and it deals 2 damage to you.
		 */
        final Player player = AllZone.getPhase().getPlayerTurn();
        final CardList cards = AllZoneUtil.getPlayerCardsInPlay(player, "Yawgmoth Demon");

        for (int i = 0; i < cards.size(); i++) {
            final Card c = cards.get(i);

            final Ability sacrificeArtifact = new Ability(c, "") {
                @Override
                public void resolve() {
                    CardList artifacts = AllZoneUtil.getPlayerCardsInPlay(player);
                    artifacts = artifacts.filter(AllZoneUtil.artifacts);

                    if (player.isHuman()) {
                        AllZone.getInputControl().setInput(new Input() {
                            private static final long serialVersionUID = -1698502376924356936L;

                            public void showMessage() {
                                AllZone.getDisplay().showMessage("Yawgmoth Demon - Select one artifact to sacrifice or be dealt 2 damage");
                                ButtonUtil.enableOnlyCancel();
                            }

                            public void selectButtonCancel() {
                                tapAndDamage(player);
                                stop();
                            }

                            public void selectCard(Card artifact, PlayerZone zone) {
                                //probably need to restrict by controller also
                                if (artifact.isArtifact() && zone.is(Constant.Zone.Battlefield)
                                        && zone.getPlayer().isHuman()) {
                                    AllZone.getGameAction().sacrifice(artifact);
                                    stop();
                                }
                            }//selectCard()
                        });//Input
                    } else { //computer
                        Card target = CardFactoryUtil.AI_getCheapestPermanent(artifacts, c, false);
                        if (null == target) {
                            tapAndDamage(player);
                        } else AllZone.getGameAction().sacrifice(target);
                    }
                }//resolve

                private void tapAndDamage(Player player) {
                    c.tap();
                    player.addDamage(2, c);
                }
            };

            StringBuilder sb = new StringBuilder();
            sb.append(c.getName()).append(" - sacrifice an artifact or ");
            sb.append(c.getName()).append(" becomes tapped and deals 2 damage to you.");
            sacrificeArtifact.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(sacrificeArtifact);

        }//end for
    }

    /**
     * <p>upkeep_Lord_of_the_Pit.</p>
     */
    private static void upkeep_Lord_of_the_Pit() {
        /*
		 * At the beginning of your upkeep, sacrifice a creature other than
		 * Lord of the Pit. If you can't, Lord of the Pit deals 7 damage to you.
		 */
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList lords = AllZoneUtil.getPlayerCardsInPlay(player, "Lord of the Pit");
        lords.addAll(AllZoneUtil.getPlayerCardsInPlay(player, "Liege of the Pit"));
        final CardList cards = lords;

        for (int i = 0; i < cards.size(); i++) {
            final Card c = cards.get(i);
            if (c.isFaceDown()) continue;

            final Ability sacrificeCreature = new Ability(c, "") {
                @Override
                public void resolve() {
                    //TODO: this should handle the case where you sacrifice 2 LOTPs to each other
                    CardList creatures = AllZoneUtil.getCreaturesInPlay(player);
                    creatures.remove(c);
                    if (player.isHuman()) {
                        AllZone.getInputControl().setInput(PlayerUtil.input_sacrificePermanent(creatures, c.getName() + " - Select a creature to sacrifice."));
                    } else { //computer
                        Card target = CardFactoryUtil.AI_getWorstCreature(creatures);
                        AllZone.getGameAction().sacrifice(target);
                    }
                }//resolve
            };

            final Ability sevenDamage = new Ability(c, "") {
                @Override
                public void resolve() {
                    player.addDamage(7, c);
                }
            };

            CardList creatures = AllZoneUtil.getCreaturesInPlay(player);
            creatures.remove(c);
            if (creatures.size() == 0) {
                //there are no creatures to sacrifice, so we must do the 7 damage

                StringBuilder sb = new StringBuilder();
                sb.append(c.getName()).append(" - deals 7 damage to controller");
                sevenDamage.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(sevenDamage);

            } else {

                StringBuilder sb = new StringBuilder();
                sb.append(c.getName()).append(" - sacrifice a creature.");
                sacrificeCreature.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(sacrificeCreature);

            }
        }//end for
    }// upkeep_Lord_of_the_Pit()

    /**
     * <p>upkeep_Drop_of_Honey.</p>
     */
    private static void upkeep_Drop_of_Honey() {
        /*
		 * At the beginning of your upkeep, destroy the creature with the
		 * least power. It can't be regenerated. If two or more creatures
		 * are tied for least power, you choose one of them.
		 */
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList drops = AllZoneUtil.getPlayerCardsInPlay(player, "Drop of Honey");
        drops.addAll(AllZoneUtil.getPlayerCardsInPlay(player, "Porphyry Nodes"));
        final CardList cards = drops;

        for (int i = 0; i < cards.size(); i++) {
            final Card c = cards.get(i);

            final Ability ability = new Ability(c, "") {
                @Override
                public void resolve() {
                    CardList creatures = AllZoneUtil.getCreaturesInPlay();
                    if (creatures.size() > 0) {
                        CardListUtil.sortAttackLowFirst(creatures);
                        int power = creatures.get(0).getNetAttack();
                        if (player.isHuman()) {
                            AllZone.getInputControl().setInput(CardFactoryUtil.input_destroyNoRegeneration(getLowestPowerList(creatures), "Select creature with power: " + power + " to sacrifice."));
                        } else { //computer
                            Card compyTarget = getCompyCardToDestroy(creatures);
                            AllZone.getGameAction().destroyNoRegeneration(compyTarget);
                        }
                    }
                }//resolve

                private CardList getLowestPowerList(CardList original) {
                    CardList lowestPower = new CardList();
                    int power = original.get(0).getNetAttack();
                    int i = 0;
                    while (i < original.size() && original.get(i).getNetAttack() == power) {
                        lowestPower.add(original.get(i));
                        i++;
                    }
                    return lowestPower;
                }

                private Card getCompyCardToDestroy(CardList original) {
                    CardList options = getLowestPowerList(original);
                    CardList humanCreatures = options.filter(new CardListFilter() {
                        public boolean addCard(Card c) {
                            return c.getController().isHuman();
                        }
                    });
                    if (humanCreatures.isEmpty()) {
                        options.shuffle();
                        return options.get(0);
                    } else {
                        humanCreatures.shuffle();
                        return humanCreatures.get(0);
                    }
                }
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append(c.getName()).append(" - destroy 1 creature with lowest power.");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }//end for
    }// upkeep_Drop_of_Honey()

    /**
     * <p>upkeep_Demonic_Hordes.</p>
     */
    private static void upkeep_Demonic_Hordes() {

        /*
		 * At the beginning of your upkeep, unless you pay BBB, 
		 * tap Demonic Hordes and sacrifice a land of an opponent's choice.
		 */

        final Player player = AllZone.getPhase().getPlayerTurn();
        final CardList cards = AllZoneUtil.getPlayerCardsInPlay(player, "Demonic Hordes");

        for (int i = 0; i < cards.size(); i++) {

            final Card c = cards.get(i);

            final Ability noPay = new Ability(c, "B B B") {
                private static final long serialVersionUID = 4820011390853920644L;

                @Override
                public void resolve() {
                    CardList playerLand = AllZoneUtil.getPlayerLandsInPlay(player);

                    c.tap();
                    if (c.getController().isComputer()) {
                        if (playerLand.size() > 0)
                            AllZone.getInputControl().setInput(PlayerUtil.input_sacrificePermanent(playerLand, c.getName() + " - Select a land to sacrifice."));
                    } else {
                        Card target = CardFactoryUtil.AI_getBestLand(playerLand);

                        AllZone.getGameAction().sacrifice(target);
                    }
                } //end resolve()
            }; //end noPay ability

            if (c.getController().isHuman()) {
                String question = "Pay Demonic Hordes upkeep cost?";
                if (GameActionUtil.showYesNoDialog(c, question)) {
                    final Ability pay = new Ability(c, "0") {
                        private static final long serialVersionUID = 4820011440853920644L;

                        public void resolve() {
                            if (AllZone.getZone(c).is(Constant.Zone.Battlefield)) {
                                StringBuilder cost = new StringBuilder();
                                cost.append("Pay cost for ").append(c).append("\r\n");
                                GameActionUtil.payManaDuringAbilityResolve(cost.toString(), noPay.getManaCost(), Command.Blank, Command.Blank);
                            }
                        } //end resolve()
                    }; //end pay ability
                    pay.setStackDescription("Demonic Hordes - Upkeep Cost");

                    AllZone.getStack().addSimultaneousStackEntry(pay);

                } //end choice
                else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(c.getName()).append(" - is tapped and you must sacrifice a land of opponent's choice");
                    noPay.setStackDescription(sb.toString());

                    AllZone.getStack().addSimultaneousStackEntry(noPay);

                }
            } //end human
            else { //computer
                if ((c.getController().isComputer() && (ComputerUtil.canPayCost(noPay)))) {
                    final Ability computerPay = new Ability(c, "0") {
                        private static final long serialVersionUID = 4820011440852868644L;

                        public void resolve() {
                            ComputerUtil.payManaCost(noPay);
                        }
                    };
                    computerPay.setStackDescription("Computer pays Demonic Hordes upkeep cost");

                    AllZone.getStack().addSimultaneousStackEntry(computerPay);

                } else {
                    AllZone.getStack().addSimultaneousStackEntry(noPay);

                }
            } //end computer

        } //end for loop

    } //upkeep_Demonic_Hordes

    //END UPKEEP CARDS

    //START ENDOFTURN CARDS

    /**
     * <p>endOfTurn_Wall_Of_Reverence.</p>
     */
    public static void endOfTurn_Wall_Of_Reverence() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Wall of Reverence");

        Ability ability;
        for (int i = 0; i < list.size(); i++) {
            final Card card = list.get(i);
            ability = new Ability(list.get(i), "0") {
                public void resolve() {
                    CardList creats = AllZoneUtil.getCreaturesInPlay(player);
                    creats = creats.filter(AllZoneUtil.getCanTargetFilter(card));
                    if (creats.size() == 0)
                        return;

                    if (player.isHuman()) {
                        Object o = GuiUtils.getChoiceOptional("Select target creature for Wall of Reverence life gain", creats.toArray());
                        if (o != null) {
                            Card c = (Card) o;
                            int power = c.getNetAttack();
                            player.gainLife(power, card);
                        }
                    } else//computer
                    {
                        CardListUtil.sortAttack(creats);
                        Card c = creats.get(0);
                        if (c != null) {
                            int power = c.getNetAttack();
                            player.gainLife(power, card);
                        }
                    }
                } // resolve
            }; // ability

            StringBuilder sb = new StringBuilder();
            sb.append(card).append(" - ").append(player).append(" gains life equal to target creature's power.");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }
    }//endOfTurn_Wall_Of_Reverence()

    /**
     * <p>endOfTurn_Predatory_Advantage.</p>
     */
    public static void endOfTurn_Predatory_Advantage() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player.getOpponent(), "Predatory Advantage");
        for (int i = 0; i < list.size(); i++) {
            final Player controller = list.get(i).getController();
            if ((player.isHuman() && Phase.getPlayerCreatureSpellCount() == 0) || (player.isComputer() && Phase.getComputerCreatureSpellCount() == 0)) {
                Ability abTrig = new Ability(list.get(i), "0") {
                    public void resolve() {
                        CardFactoryUtil.makeToken("Lizard", "G 2 2 Lizard", controller, "G", new String[]{"Creature", "Lizard"}, 2, 2, new String[]{""});
                    }
                };
                abTrig.setTrigger(true);
                abTrig.setStackDescription("At the beginning of each opponent's end step, if that player didn't cast a creature spell this turn, put a 2/2 green Lizard creature token onto the battlefield.");

                AllZone.getGameAction().playSpellAbility(abTrig);
            }
        }
    }

    /**
     * <p>endOfTurn_Lighthouse_Chronologist.</p>
     */
    public static void endOfTurn_Lighthouse_Chronologist() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        final Player opponent = player.getOpponent();
        CardList list = AllZoneUtil.getPlayerCardsInPlay(opponent);

        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.getName().equals("Lighthouse Chronologist") && c.getCounters(Counters.LEVEL) >= 7;
            }
        });

        Ability ability;
        for (int i = 0; i < list.size(); i++) {
            final Card card = list.get(i);
            ability = new Ability(list.get(i), "0") {
                public void resolve() {
                    AllZone.getPhase().addExtraTurn(card.getController());
                }
            };

            StringBuilder sb = new StringBuilder();
            sb.append(card).append(" - ").append(card.getController()).append(" takes an extra turn.");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }
    }

    /**
     * <p>endOfTurn_Krovikan_Horror.</p>
     */
    public static void endOfTurn_Krovikan_Horror() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        final Player opponent = player.getOpponent();
        horrorReturn(player);
        horrorReturn(opponent);
    }

    /**
     * <p>horrorReturn.</p>
     *
     * @param player a {@link forge.Player} object.
     */
    public static void horrorReturn(Player player) {
        // Find each Horror, peek at the card above it, if it's a creature return to hand
        CardList grave = AllZoneUtil.getPlayerGraveyard(player);
        if (grave.getName("Krovikan Horror").size() == 0) return;
        int i = 0;

        while (i + 1 < grave.size()) {
            Card c = grave.get(i);
            ArrayList<String> types = grave.get(i + 1).getType();
            if (c.getName().equals("Krovikan Horror") && types.contains("Creature")) {

                if (player.isHuman()) {
                    String question = "Return Krovikan Horror to your hand?";
                    if (GameActionUtil.showYesNoDialog(c, question)) {
                        AllZone.getGameAction().moveToHand(c);
                        grave.remove(c);
                    }
                    // increment counter to next occurance of Krovikan Horror
                    // if human decides not to return Krovikan Horror to hand
                    else
                        i++;
                }
                // player is computer
                else {
                    AllZone.getGameAction().moveToHand(c);
                    grave.remove(c);
                }
            } else
                i++;
        }
    }
    //END ENDOFTURN CARDS

    /**
     * <p>removeAttackedBlockedThisTurn.</p>
     */
    public static void removeAttackedBlockedThisTurn() {
        // resets the status of attacked/blocked this turn
        Player player = AllZone.getPhase().getPlayerTurn();
        CardList list = AllZoneUtil.getCreaturesInPlay(player);

        for (int i = 0; i < list.size(); i++) {
            Card c = list.get(i);
            if (c.getCreatureAttackedThisCombat()) c.setCreatureAttackedThisCombat(false);
            if (c.getCreatureBlockedThisCombat()) c.setCreatureBlockedThisCombat(false);
            //do not reset setCreatureAttackedThisTurn(), this appears to be combat specific

            if (c.getCreatureGotBlockedThisCombat()) c.setCreatureGotBlockedThisCombat(false);
        }

        AllZone.getGameInfo().setAssignedFirstStrikeDamageThisCombat(false);
        AllZone.getGameInfo().setResolvedFirstStrikeDamageThisCombat(false);
    }

    /**
     * <p>showYesNoDialog.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param question a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean showYesNoDialog(Card c, String question) {
        return showYesNoDialog(c, question, false);
    }
    
    /**
     * <p>showYesNoDialog.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param question a {@link java.lang.String} object.
     * @param defaultNo true if the default option should be "No", false otherwise
     * @return a boolean.
     */
    public static boolean showYesNoDialog(Card c, String question, boolean defaultNo) {
        AllZone.getDisplay().setCard(c);
        StringBuilder title = new StringBuilder();
        title.append(c.getName()).append(" - Ability");

        if (!(question.length() > 0)) {
            question = "Activate card's ability?";
        }

        int answer;
        if(defaultNo) {
        	Object options[] = {"Yes", "No"};
        	answer = JOptionPane.showOptionDialog(null, question, title.toString(), JOptionPane.YES_NO_OPTION,
        				JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
        }
        else {
        	answer= JOptionPane.showConfirmDialog(null, question, title.toString(), JOptionPane.YES_NO_OPTION);
        }

        if (answer == JOptionPane.YES_OPTION) return true;
        else return false;
    }

    /**
     * <p>showInfoDialg.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public static void showInfoDialg(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * <p>flipACoin.</p>
     *
     * @param caller a {@link forge.Player} object.
     * @param source a {@link forge.Card} object.
     * @return a boolean.
     */
    public static boolean flipACoin(Player caller, Card source) {
        String choice = "";
        String choices[] = {"heads", "tails"};
        boolean flip = (50 > MyRandom.random.nextInt(100));
        if (caller.isHuman()) {
            choice = (String) GuiUtils.getChoice(source.getName() + " - Call coin flip", choices);
        } else {
            choice = choices[MyRandom.random.nextInt(2)];
        }

        if ((flip == true && choice.equals("heads")) || (flip == false && choice.equals("tails"))) {
            JOptionPane.showMessageDialog(null, source.getName() + " - " + caller + " wins flip.", source.getName(), JOptionPane.PLAIN_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, source.getName() + " - " + caller + " loses flip.", source.getName(), JOptionPane.PLAIN_MESSAGE);
            return false;
        }
    }

    /**
     * <p>executeLandfallEffects.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void executeLandfallEffects(Card c) {
        if (c.getName().equals("Lotus Cobra")) landfall_Lotus_Cobra(c);
    }

    /**
     * <p>showLandfallDialog.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a boolean.
     */
    private static boolean showLandfallDialog(Card c) {
        AllZone.getDisplay().setCard(c);
        String[] choices = {"Yes", "No"};

        Object q = null;

        q = GuiUtils.getChoiceOptional("Use " + c.getName() + " Landfall?", choices);

        if (q == null || q.equals("No")) return false;
        else return true;
    }

    /**
     * <p>landfall_Lotus_Cobra.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    private static void landfall_Lotus_Cobra(final Card c) {
        Ability ability = new Ability(c, "0") {
            @Override
            public void resolve() {
                String color = "";

                Object o = GuiUtils.getChoice("Choose mana color", Constant.Color.onlyColors);
                color = Input_PayManaCostUtil.getShortColorString((String) o);

                Ability_Mana abMana = new Ability_Mana(c, "0", color) {
                    private static final long serialVersionUID = -2182129023960978132L;
                };
                abMana.produceMana();
            }
        };


        StringBuilder sb = new StringBuilder();
        sb.append(c.getName()).append(" - add one mana of any color to your mana pool.");
        ability.setStackDescription(sb.toString());

        if (c.getController().isHuman()) {
            if (showLandfallDialog(c)) AllZone.getStack().addSimultaneousStackEntry(ability);
        } else {
            // TODO: once AI has a mana pool he should choose add Ability and choose a mana as appropriate
        }
    }

    //not restricted to combat damage, not restricted to dealing damage to creatures/players
    /**
     * <p>executeDamageDealingEffects.</p>
     *
     * @param source a {@link forge.Card} object.
     * @param damage a int.
     */
    public static void executeDamageDealingEffects(final Card source, int damage) {

        if (damage <= 0) return;

        if (source.hasKeyword("Lifelink")) source.getController().gainLife(damage, source);

    }

    //restricted to combat damage and dealing damage to creatures
    /**
     * <p>executeCombatDamageToCreatureEffects.</p>
     *
     * @param source a {@link forge.Card} object.
     * @param affected a {@link forge.Card} object.
     * @param damage a int.
     */
    public static void executeCombatDamageToCreatureEffects(final Card source, final Card affected, int damage) {

        if (damage <= 0) return;

        //placeholder for any future needs (everything that was here is converted to script)
    }

    //not restricted to combat damage, restricted to dealing damage to creatures
    /**
     * <p>executeDamageToCreatureEffects.</p>
     *
     * @param source a {@link forge.Card} object.
     * @param affected a {@link forge.Card} object.
     * @param damage a int.
     */
    public static void executeDamageToCreatureEffects(final Card source, final Card affected, int damage) {

        if (damage <= 0) return;

        if (affected.getName().equals("Stuffy Doll")) {
            final Player opponent = affected.getOwner().getOpponent();
            final int stuffyDamage = damage;
            SpellAbility ability = new Ability(affected, "0") {
                @Override
                public void resolve() {
                    opponent.addDamage(stuffyDamage, affected);
                }
            };
            StringBuilder sb = new StringBuilder();
            sb.append(affected.getName() + " - Deals ").append(stuffyDamage).append(" damage to ").append(opponent);
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }

        if (affected.hasKeyword("Whenever CARDNAME is dealt damage, put a +1/+1 counter on it.")) {
            Ability ability2 = new Ability(affected, "0") {
                @Override
                public void resolve() {
                    affected.addCounter(Counters.P1P1, 1);
                }
            }; // ability2

            StringBuilder sb2 = new StringBuilder();
            sb2.append(affected.getName()).append(" - gets a +1/+1 counter");
            ability2.setStackDescription(sb2.toString());
            int amount = affected.getAmountOfKeyword("Whenever CARDNAME is dealt damage, put a +1/+1 counter on it.");

            for (int i = 0; i < amount; i++)
                AllZone.getStack().addSimultaneousStackEntry(ability2);

        }

        if (affected.hasStartOfKeyword("When CARDNAME is dealt damage, destroy it.")) {
            final Ability ability = new Ability(source, "0") {
                @Override
                public void resolve() {
                    AllZone.getGameAction().destroy(affected);
                }
            };

            final Ability ability2 = new Ability(source, "0") {
                @Override
                public void resolve() {
                    AllZone.getGameAction().destroyNoRegeneration(affected);
                }
            };

            StringBuilder sb = new StringBuilder();
            sb.append(affected).append(" - destroy");
            ability.setStackDescription(sb.toString());
            ability2.setStackDescription(sb.toString());

            if (affected.hasKeyword("When CARDNAME is dealt damage, destroy it. It can't be regenerated.")) {
                int amount = affected.getAmountOfKeyword("When CARDNAME is dealt damage, destroy it. It can't be regenerated.");

                for (int i = 0; i < amount; i++)
                    AllZone.getStack().addSimultaneousStackEntry(ability2);

            }
            int amount = affected.getAmountOfKeyword("When CARDNAME is dealt damage, destroy it.");

            for (int i = 1; i < amount; i++)
                AllZone.getStack().addSimultaneousStackEntry(ability);
            AllZone.getStack().addSimultaneousStackEntry(ability);
        }

        if (source.hasKeyword("Deathtouch")
                && affected.isCreature())
            AllZone.getGameAction().destroy(affected);


    }

    /**
     * <p>executeSwordOfLightAndShadowEffects.</p>
     *
     * @param source a {@link forge.Card} object.
     */
    public static void executeSwordOfLightAndShadowEffects(final Card source) {
        final Card src = source;
        final Ability ability = new Ability(src, "0") {
            @Override
            public void resolve() {
                Card target = getTargetCard();
                if (target != null) {
                    if (AllZoneUtil.isCardInPlayerGraveyard(src.getController(), target)) {
                        PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, src.getController());
                        AllZone.getGameAction().moveTo(hand, target);
                    }
                }

                src.getController().gainLife(3, source);
            }
        }; // ability

        Command res = new Command() {
            private static final long serialVersionUID = -7433708170033536384L;

            public void execute() {
                CardList list = AllZoneUtil.getPlayerGraveyard(src.getController());
                list = list.filter(AllZoneUtil.creatures);

                if (list.isEmpty()) {
                    AllZone.getStack().addSimultaneousStackEntry(ability);

                    return;
                }

                if (src.getController().isHuman()) {
                    Object o = GuiUtils.getChoiceOptional("Select target card", list.toArray());
                    if (o != null) {
                        ability.setTargetCard((Card) o);
                        AllZone.getStack().addSimultaneousStackEntry(ability);

                    }
                }//if
                else//computer
                {
                    Card best = CardFactoryUtil.AI_getBestCreature(list);
                    ability.setTargetCard(best);
                    AllZone.getStack().addSimultaneousStackEntry(ability);

                }
            }//execute()
        };//Command

        StringBuilder sb = new StringBuilder();
        sb.append("Sword of Light and Shadow - You gain 3 life and you may return ");
        sb.append("up to one target creature card from your graveyard to your hand");
        ability.setStackDescription(sb.toString());

        res.execute();
    }

    //this is for cards like Sengir Vampire
    /**
     * <p>executeVampiricEffects.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    public static void executeVampiricEffects(Card c) {
        ArrayList<String> a = c.getKeyword();
        for (int i = 0; i < a.size(); i++) {
            if (AllZoneUtil.isCardInPlay(c)
                    && a.get(i).toString().startsWith(
                    "Whenever a creature dealt damage by CARDNAME this turn is put into a graveyard, put")) {
                final Card thisCard = c;
                final String kw = a.get(i).toString();
                Ability ability2 = new Ability(c, "0") {
                    @Override
                    public void resolve() {
                        Counters counter = Counters.P1P1;
                        if (kw.contains("+2/+2")) counter = Counters.P2P2;
                        if (AllZoneUtil.isCardInPlay(thisCard)) thisCard.addCounter(counter, 1);
                    }
                }; // ability2

                StringBuilder sb = new StringBuilder();
                sb.append(c.getName());
                if (kw.contains("+2/+2")) {
                    sb.append(" - gets a +2/+2 counter");
                } else {
                    sb.append(" - gets a +1/+1 counter");
                }
                ability2.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(ability2);

            }
        }
    }

    //not restricted to just combat damage, restricted to players
    /**
     * <p>executeDamageToPlayerEffects.</p>
     *
     * @param player a {@link forge.Player} object.
     * @param c a {@link forge.Card} object.
     * @param damage a int.
     */
    public static void executeDamageToPlayerEffects(final Player player, final Card c, final int damage) {
        if (damage <= 0) return;

        CardList playerPerms = AllZoneUtil.getPlayerCardsInPlay(player);

        if (playerPerms.getName("Farsight Mask").size() > 0) {
            final Card c1 = c;
            CardList l = playerPerms.filter(new CardListFilter() {
                public boolean addCard(Card crd) {
                    return crd.getName().equals("Farsight Mask") && crd.isUntapped() && !c1.getController().equals(crd.getController());
                }
            });
            for (Card crd : l)
                playerDamage_Farsight_Mask(player, c, crd);
        }

        if (AllZoneUtil.isCardInPlay("Lich", player)) {
            CardList lichs = playerPerms.getName("Lich");
            for (Card crd : lichs) {
                final Card lich = crd;
                SpellAbility ability = new Ability(lich, "0") {
                    public void resolve() {
                        for (int i = 0; i < damage; i++) {
                            CardList nonTokens = AllZoneUtil.getPlayerCardsInPlay(player);
                            nonTokens = nonTokens.filter(AllZoneUtil.nonToken);
                            if (nonTokens.size() == 0) {
                                player.altLoseConditionMet("Lich");
                            } else player.sacrificePermanent("Select a permanent to sacrifice", nonTokens);
                        }
                    }
                };

                StringBuilder sb = new StringBuilder();
                sb.append(lich.getName()).append(" - ").append(lich.getController());
                sb.append(" sacrifices ").append(damage).append(" nontoken Permanents.");
                ability.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }

        if (c.getName().equals("Whirling Dervish") || c.getName().equals("Dunerider Outlaw"))
            playerCombatDamage_Whirling_Dervish(c);

        if (player.isPlayer(AllZone.getHumanPlayer())) c.setDealtDmgToHumanThisTurn(true);
        if (player.isPlayer(AllZone.getComputerPlayer())) c.setDealtDmgToComputerThisTurn(true);
    }


    //restricted to combat damage, restricted to players
    /**
     * <p>executeCombatDamageToPlayerEffects.</p>
     *
     * @param player a {@link forge.Player} object.
     * @param c a {@link forge.Card} object.
     * @param damage a int.
     */
    public static void executeCombatDamageToPlayerEffects(final Player player, final Card c, final int damage) {

        if (damage <= 0) return;

        if (c.isCreature() && AllZoneUtil.isCardInPlay("Contested War Zone", player)) {
            CardList zones = AllZoneUtil.getPlayerCardsInPlay(player, "Contested War Zone");
            for (final Card zone : zones) {
                Ability ability = new Ability(zone, "0") {
                    @Override
                    public void resolve() {
                        if (AllZoneUtil.isCardInPlay(zone)) {
                            AllZone.getGameAction().changeController(new CardList(zone), zone.getController(), c.getController());
                        }
                    }
                };
                ability.setStackDescription(zone + " - " + c.getController() + " gains control of " + zone);

                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }

        if (c.hasStartOfKeyword("Poisonous")) {
            int KeywordPosition = c.getKeywordPosition("Poisonous");
            String parse = c.getKeyword().get(KeywordPosition).toString();
            String k[] = parse.split(" ");
            final int poison = Integer.parseInt(k[1]);
            final Card crd = c;

            Ability ability = new Ability(c, "0") {
                public void resolve() {
                    final Player player = crd.getController();
                    final Player opponent = player.getOpponent();

                    if (opponent.isHuman())
                        AllZone.getHumanPlayer().addPoisonCounters(poison);
                    else
                        AllZone.getComputerPlayer().addPoisonCounters(poison);
                }
            };

            StringBuilder sb = new StringBuilder();
            sb.append(c);
            sb.append(" - Poisonous: ");
            sb.append(c.getController().getOpponent());
            sb.append(" gets ");
            sb.append(poison);
            sb.append(" poison counters.");

            ability.setStackDescription(sb.toString());
            ArrayList<String> keywords = c.getKeyword();

            for (int i = 0; i < keywords.size(); i++) {
                if (keywords.get(i).startsWith("Poisonous"))
                    AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }

        if (CardFactoryUtil.hasNumberEquipments(c, "Quietus Spike") > 0 && c.getNetAttack() > 0) {
            for (int k = 0; k < CardFactoryUtil.hasNumberEquipments(c, "Quietus Spike"); k++) {
                playerCombatDamage_lose_halflife_up(c);
            }
        }

        if (c.isEquipped()) {
            ArrayList<Card> equips = c.getEquippedBy();
            for (Card equip : equips) {
                if (equip.getName().equals("Sword of Light and Shadow")) {
                    GameActionUtil.executeSwordOfLightAndShadowEffects(equip);
                }
            }
        }//isEquipped


        if (c.getName().equals("Scalpelexis")) playerCombatDamage_Scalpelexis(c);
        else if (c.getName().equals("Augury Adept")) playerCombatDamage_Augury_Adept(c);
        else if (c.getName().equals("Spawnwrithe")) playerCombatDamage_Spawnwrithe(c);
        else if (c.getName().equals("Treva, the Renewer")) playerCombatDamage_Treva(c);
        else if (c.getName().equals("Rith, the Awakener")) playerCombatDamage_Rith(c);

        else if (c.isEnchantedBy("Celestial Mantle")) execute_Celestial_Mantle(c);

        //Unused variable
        //c.setDealtCombatDmgToOppThisTurn(true); 

    }//executeCombatDamageToPlayerEffects

    /**
     * <p>execute_Celestial_Mantle.</p>
     *
     * @param enchanted a {@link forge.Card} object.
     */
    private static void execute_Celestial_Mantle(final Card enchanted) {
        ArrayList<Card> auras = enchanted.getEnchantedBy();
        for (final Card aura : auras) {
            if (aura.getName().equals("Celestial Mantle")) {
                Ability doubleLife = new Ability(aura, "0") {
                    public void resolve() {
                        int life = enchanted.getController().getLife();
                        enchanted.getController().setLife(life * 2, aura);
                    }
                };
                doubleLife.setStackDescription(aura.getName() + " - " + enchanted.getController() + " doubles his or her life total.");

                AllZone.getStack().addSimultaneousStackEntry(doubleLife);

            }
        }
    }

    /**
     * <p>playerDamage_Farsight_Mask.</p>
     *
     * @param player a {@link forge.Player} object.
     * @param c a {@link forge.Card} object.
     * @param crd a {@link forge.Card} object.
     * @param crd a {@link forge.Card} object.
     */
    private static void playerDamage_Farsight_Mask(final Player player, final Card c, final Card crd) {
        Ability ability = new Ability(crd, "0") {
            public void resolve() {
                if (crd.isUntapped()) {
                    player.mayDrawCard();
                }
            }
        };
        ability.setStackDescription("Farsight Mask - You may draw a card.");

        AllZone.getStack().addSimultaneousStackEntry(ability);

    }


    /**
     * <p>playerCombatDamage_Treva.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    private static void playerCombatDamage_Treva(Card c) {
        SpellAbility[] sa = c.getSpellAbility();
        if (c.getController().isHuman()) AllZone.getGameAction().playSpellAbility(sa[1]);
        else ComputerUtil.playNoStack(sa[1]);

    }

    /**
     * <p>playerCombatDamage_Rith.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    private static void playerCombatDamage_Rith(Card c) {
        SpellAbility[] sa = c.getSpellAbility();
        if (c.getController().isHuman()) AllZone.getGameAction().playSpellAbility(sa[1]);
        else ComputerUtil.playNoStack(sa[1]);
    }

    /**
     * <p>playerCombatDamage_Whirling_Dervish.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    private static void playerCombatDamage_Whirling_Dervish(Card c) {
        final int power = c.getNetAttack();
        final Card card = c;

        if (power > 0) {
            final Ability ability2 = new Ability(c, "0") {
                @Override
                public void resolve() {
                    card.addCounter(Counters.P1P1, 1);
                }
            };// ability2

            StringBuilder sb = new StringBuilder();
            sb.append(c.getName()).append(" - gets a +1/+1 counter.");
            ability2.setStackDescription(sb.toString());

            Command dealtDmg = new Command() {
                private static final long serialVersionUID = 2200679209414069339L;

                public void execute() {
                    AllZone.getStack().addSimultaneousStackEntry(ability2);

                }
            };
            AllZone.getEndOfTurn().addAt(dealtDmg);

        } // if
    }

    /**
     * <p>playerCombatDamage_lose_halflife_up.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    private static void playerCombatDamage_lose_halflife_up(Card c) {
        final Player player = c.getController();
        final Player opponent = player.getOpponent();
        final Card F_card = c;
        if (c.getNetAttack() > 0) {
            Ability ability2 = new Ability(c, "0") {
                @Override
                public void resolve() {
                    int x = 0;
                    int y = 0;
                    if (player.isHuman()) {
                        y = (AllZone.getComputerPlayer().getLife() % 2);
                        if (!(y == 0)) y = 1;
                        else y = 0;

                        x = (AllZone.getComputerPlayer().getLife() / 2) + y;
                    } else {
                        y = (AllZone.getHumanPlayer().getLife() % 2);
                        if (!(y == 0)) y = 1;
                        else y = 0;

                        x = (AllZone.getHumanPlayer().getLife() / 2) + y;
                    }
                    opponent.loseLife(x, F_card);

                }
            };// ability2

            StringBuilder sb = new StringBuilder();
            sb.append(c.getName()).append(" - ").append(opponent);
            sb.append(" loses half his or her life, rounded up.");
            ability2.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability2);

        }
    }

    /**
     * <p>playerCombatDamage_Scalpelexis.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    private static void playerCombatDamage_Scalpelexis(Card c) {
        final Player player = c.getController();
        final Player opponent = player.getOpponent();

        if (c.getNetAttack() > 0) {
            Ability ability = new Ability(c, "0") {
                @Override
                public void resolve() {

                    CardList libList = AllZoneUtil.getPlayerCardsInLibrary(opponent);
                    int count = 0;
                    int broken = 0;
                    for (int i = 0; i < libList.size(); i = i + 4) {
                        Card c1 = null;
                        Card c2 = null;
                        Card c3 = null;
                        Card c4 = null;
                        if (i < libList.size()) c1 = libList.get(i);
                        else broken = 1;
                        if (i + 1 < libList.size()) c2 = libList.get(i + 1);
                        else broken = 1;
                        if (i + 2 < libList.size()) c3 = libList.get(i + 2);
                        else broken = 1;
                        if (i + 3 < libList.size()) c4 = libList.get(i + 3);
                        else broken = 1;
                        if (broken == 0) {
                            if ((c1.getName().contains(c2.getName()) || c1.getName().contains(c3.getName())
                                    || c1.getName().contains(c4.getName()) || c2.getName().contains(c3.getName())
                                    || c2.getName().contains(c4.getName()) || c3.getName().contains(c4.getName()))) {
                                count = count + 1;
                            } else {
                                broken = 1;
                            }
                        }

                    }
                    count = (count * 4) + 4;
                    int max = count;
                    if (libList.size() < count) max = libList.size();

                    for (int j = 0; j < max; j++) {
                        Card c = libList.get(j);
                        AllZone.getGameAction().exile(c);
                    }
                }
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Scalpelexis - ").append(opponent);
            sb.append(" exiles the top four cards of his or her library. ");
            sb.append("If two or more of those cards have the same name, repeat this process.");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }
    }

    /**
     * <p>playerCombatDamage_Spawnwrithe.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    private static void playerCombatDamage_Spawnwrithe(Card c) {
        final Player player = c.getController();
        final Card crd = c;

        Ability ability2 = new Ability(c, "0") {
            @Override
            public void resolve() {
                CardList cl = CardFactoryUtil.makeToken("Spawnwrithe", "", crd.getController(), "2 G", new String[]{
                        "Creature", "Elemental"}, 2, 2, new String[]{"Trample"});

                for (Card c : cl) {
                    c.setText("Whenever Spawnwrithe deals combat damage to a player, put a token that's a copy of Spawnwrithe onto the battlefield.");
                    c.setCopiedToken(true);
                }
            }
        };// ability2

        StringBuilder sb = new StringBuilder();
        sb.append(c.getName()).append(" - ").append(player).append(" puts copy onto the battlefield.");
        ability2.setStackDescription(sb.toString());

        AllZone.getStack().addSimultaneousStackEntry(ability2);

    }

    /**
     * <p>playerCombatDamage_Augury_Adept.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    private static void playerCombatDamage_Augury_Adept(Card c) {
        final Player[] player = new Player[1];
        final Card crd = c;

        if (c.getNetAttack() > 0) {
            Ability ability2 = new Ability(crd, "0") {
                @Override
                public void resolve() {
                    player[0] = crd.getController();
                    PlayerZone lib = AllZone.getZone(Constant.Zone.Library, player[0]);

                    if (lib.size() > 0) {
                        CardList cl = new CardList();
                        cl.add(lib.get(0));
                        GuiUtils.getChoiceOptional("Top card", cl.toArray());
                    }
                    ;
                    if (lib.size() == 0)
                        return;
                    Card top = lib.get(0);
                    player[0].gainLife(CardUtil.getConvertedManaCost(top.getManaCost()), crd);
                    AllZone.getGameAction().moveToHand(top);
                }
            };// ability2

            player[0] = c.getController();

            StringBuilder sb = new StringBuilder();
            sb.append(c.getName()).append(" - ").append(player[0]);
            sb.append(" reveals the top card of his library and put that card into his hand. ");
            sb.append("He gain life equal to its converted mana cost.");
            ability2.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability2);

        }
    }

    /**
     * <p>upkeep_AI_Aluren.</p>
     */
    private static void upkeep_AI_Aluren() {
        CardList alurens = AllZoneUtil.getCardsInPlay("Aluren");
        if (alurens.size() == 0)
            return;

        CardList inHand = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer());
        inHand = inHand.getType("Creature");
        CardList playable = new CardList();

        for (Card c : inHand)
            if (CardUtil.getConvertedManaCost(c.getManaCost()) <= 3)
                playable.add(c);

        for (Card c : playable)
            AllZone.getGameAction().playSpellAbilityForFree(c.getSpellPermanent());
    }


    /**
     * <p>upkeep_Dance_of_the_Dead.</p>
     */
    private static void upkeep_Dance_of_the_Dead() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList dances = AllZoneUtil.getPlayerCardsInPlay(player, "Dance of the Dead");
        for (Card dance : dances) {
            final Card source = dance;
            final ArrayList<Card> list = source.getEnchanting();
            final Card creature = list.get(0);
            if (creature.isTapped()) {
                Ability vaultChoice = new Ability(source, "0") {

                    @Override
                    public void resolve() {
                        if (GameActionUtil.showYesNoDialog(source, "Untap " + creature.getName() + "?")) {
                            //prompt for pay mana cost, then untap
                            final SpellAbility untap = new Ability(source, "1 B") {
                                @Override
                                public void resolve() {
                                    creature.untap();
                                }
                            };//Ability

                            StringBuilder sb = new StringBuilder();
                            sb.append("Untap ").append(creature);
                            untap.setStackDescription(sb.toString());

                            AllZone.getGameAction().playSpellAbility(untap);
                        }
                    }
                };
                vaultChoice.setStackDescription(source.getName() + " - Untap creature during Upkeep?");

                AllZone.getStack().addSimultaneousStackEntry(vaultChoice);

            }
        }
    }

    
    /////////////////////////
    // Start of Kinship cards
    /////////////////////////


    /**
     * <p>upkeep_Ink_Dissolver.</p>
     */
    private static void upkeep_Ink_Dissolver() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        final Player opponent = player.getOpponent();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Ink Dissolver");

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Merfolk", "Wizard"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantToMillOpponent = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and opponent puts the top 3 ");
                            question.append("cards of his library into his graveyard?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantToMillOpponent = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantToMillOpponent = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }

                    if (wantToMillOpponent)
                        opponent.mill(3);
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Ink Dissolver - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Ink_Dissolver()


    /**
     * <p>upkeep_Kithkin_Zephyrnaut.</p>
     */
    private static void upkeep_Kithkin_Zephyrnaut() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Kithkin Zephyrnaut");

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Kithkin", "Soldier"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantKithkinBuff = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card, Kithkin Zephyrnaut gets +2/+2 and ");
                            question.append("gains flying and vigilance until end of turn?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantKithkinBuff = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantKithkinBuff = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }

                    if (wantKithkinBuff) {
                        k.addTempAttackBoost(2);
                        k.addTempDefenseBoost(2);
                        k.addExtrinsicKeyword("Flying");
                        k.addExtrinsicKeyword("Vigilance");

                        final Command untilEOT = new Command() {
                            private static final long serialVersionUID = 213717084767008154L;

                            public void execute() {
                                k.addTempAttackBoost(-2);
                                k.addTempDefenseBoost(-2);
                                k.removeExtrinsicKeyword("Flying");
                                k.removeExtrinsicKeyword("Vigilance");
                            }
                        };
                        AllZone.getEndOfTurn().addUntil(untilEOT);
                    }
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Kithkin Zephyrnaut - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Kithkin_Zephyrnaut()


    /**
     * <p>upkeep_Leaf_Crowned_Elder.</p>
     */
    private static void upkeep_Leaf_Crowned_Elder() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Leaf-Crowned Elder");

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Treefolk", "Shaman"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantToPlayCard = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal and play this card without paying its mana cost?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantToPlayCard = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantToPlayCard = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }

                    if (wantToPlayCard) {
                        if (player.isHuman()) {
                            Card c = library.get(0);
                            AllZone.getGameAction().playCardNoCost(c);
                        }
                        // player isComputer()
                        else {
                            Card c = library.get(0);
                            ArrayList<SpellAbility> choices = c.getBasicSpells();

                            for (SpellAbility sa : choices) {
                                if (sa.canPlayAI()) {
                                    ComputerUtil.playStackFree(sa);
                                    break;
                                }
                            }
                        }
                    }// wantToPlayCard
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Leaf-Crowned Elder - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Leaf_Crowned_Elder()


    /**
     * <p>upkeep_Mudbutton_Clanger.</p>
     */
    private static void upkeep_Mudbutton_Clanger() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Mudbutton Clanger");

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Goblin", "Warrior"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantGoblinBuff = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and Mudbutton Clanger gets +1/+1 until end of turn?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantGoblinBuff = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantGoblinBuff = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }

                    if (wantGoblinBuff) {
                        k.addTempAttackBoost(1);
                        k.addTempDefenseBoost(1);

                        final Command untilEOT = new Command() {
                            private static final long serialVersionUID = -103560515951630426L;

                            public void execute() {
                                k.addTempAttackBoost(-1);
                                k.addTempDefenseBoost(-1);
                            }
                        };
                        AllZone.getEndOfTurn().addUntil(untilEOT);
                    }
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Mudbutton Clanger - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Mudbutton_Clanger()


    /**
     * <p>upkeep_Nightshade_Schemers.</p>
     */
    private static void upkeep_Nightshade_Schemers() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Nightshade Schemers");
        final Player opponent = player.getOpponent();

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Faerie", "Wizard"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantOpponentLoseLife = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and opponent loses 2 life?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantOpponentLoseLife = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantOpponentLoseLife = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }
                    if (wantOpponentLoseLife)
                        opponent.loseLife(2, k);
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Nightshade Schemers - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Nightshade_Schemers()


    /**
     * <p>upkeep_Pyroclast_Consul.</p>
     */
    private static void upkeep_Pyroclast_Consul() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Pyroclast Consul");

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Elemental", "Shaman"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantDamageCreatures = false;
                    String[] smallCreatures = {"Creature.toughnessLE2"};

                    CardList humanCreatures = AllZoneUtil.getCreaturesInPlay(AllZone.getHumanPlayer());
                    humanCreatures = humanCreatures.getValidCards(smallCreatures, k.getController(), k);
                    humanCreatures = humanCreatures.getNotKeyword("Indestructible");

                    CardList computerCreatures = AllZoneUtil.getCreaturesInPlay(AllZone.getComputerPlayer());
                    computerCreatures = computerCreatures.getValidCards(smallCreatures, k.getController(), k);
                    computerCreatures = computerCreatures.getNotKeyword("Indestructible");

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and Pyroclast Consul deals 2 damage to each creature?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantDamageCreatures = true;
                            }
                        }
                        // player isComputer()
                        else {
                            if (humanCreatures.size() > computerCreatures.size()) {
                                String title = "Computer reveals";
                                revealTopCard(title);
                                wantDamageCreatures = true;
                            }
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }

                    if (wantDamageCreatures) {
                        CardList allCreatures = AllZoneUtil.getCreaturesInPlay();
                        for (final Card crd : allCreatures) {
                            crd.addDamage(2, k);
                        }
                    }
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Pyroclast Consul - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Pyroclast_Consul()


    /**
     * <p>upkeep_Sensation_Gorger.</p>
     */
    private static void upkeep_Sensation_Gorger() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Sensation Gorger");
        final Player opponent = player.getOpponent();

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Goblin", "Shaman"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    PlayerZone hand = AllZone.getZone(Constant.Zone.Hand, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantDiscardThenDraw = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and have both players discard their hand and draw 4 cards?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantDiscardThenDraw = true;
                            }
                        }
                        // player isComputer()
                        else {
                            if (library.size() > 4 && hand.size() < 2) {
                                String title = "Computer reveals";
                                revealTopCard(title);
                                wantDiscardThenDraw = true;
                            }
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }
                    if (wantDiscardThenDraw) {
                        player.discardHand(this);
                        opponent.discardHand(this);

                        player.drawCards(4);
                        opponent.drawCards(4);
                    }
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Sensation Gorger - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Sensation_Gorger()


    /**
     * <p>upkeep_Squeaking_Pie_Grubfellows.</p>
     */
    private static void upkeep_Squeaking_Pie_Grubfellows() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Squeaking Pie Grubfellows");
        final Player opponent = player.getOpponent();

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Goblin", "Shaman"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantOpponentDiscard = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and have opponent discard a card?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantOpponentDiscard = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantOpponentDiscard = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }

                    if (wantOpponentDiscard) {
                        opponent.discard(this);
                    }
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Squeaking Pie Grubfellows - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Squeaking_Pie_Grubfellows()


    /**
     * <p>upkeep_Wandering_Graybeard.</p>
     */
    private static void upkeep_Wandering_Graybeard() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Wandering Graybeard");

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Giant", "Wizard"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantGainLife = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and gain 4 life?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantGainLife = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantGainLife = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }
                    if (wantGainLife)
                        player.gainLife(4, k);
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Wandering Graybeard - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Wandering_Graybeard()


    /**
     * <p>upkeep_Waterspout_Weavers.</p>
     */
    private static void upkeep_Waterspout_Weavers() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Waterspout Weavers");

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Merfolk", "Wizard"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantMerfolkBuff = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and each creature you ");
                            question.append("control gains flying until end of turn?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantMerfolkBuff = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantMerfolkBuff = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }

                    if (wantMerfolkBuff) {
                        CardList creatures = AllZoneUtil.getCreaturesInPlay(player);
                        for (int i = 0; i < creatures.size(); i++) {
                            if (!creatures.get(i).hasKeyword("Flying")) {
                                creatures.get(i).addExtrinsicKeyword("Flying");
                            }
                        }
                        final Command untilEOT = new Command() {
                            private static final long serialVersionUID = -1978446996943583910L;

                            public void execute() {
                                CardList creatures = AllZoneUtil.getCreaturesInPlay(player);
                                for (int i = 0; i < creatures.size(); i++) {
                                    if (creatures.get(i).hasKeyword("Flying")) {
                                        creatures.get(i).removeExtrinsicKeyword("Flying");
                                    }
                                }
                            }
                        };
                        AllZone.getEndOfTurn().addUntil(untilEOT);
                    }
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Waterspout Weavers - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Waterspout_Weavers()


    /**
     * <p>upkeep_Winnower_Patrol.</p>
     */
    private static void upkeep_Winnower_Patrol() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Winnower Patrol");

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Elf", "Warrior"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantCounter = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and put a +1/+1 counter on Winnower Patrol?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantCounter = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantCounter = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }
                    if (wantCounter)
                        k.addCounter(Counters.P1P1, 1);
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Winnower Patrol - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Winnower_Patrol()


    /**
     * <p>upkeep_Wolf_Skull_Shaman.</p>
     */
    private static void upkeep_Wolf_Skull_Shaman() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList kinship = AllZoneUtil.getPlayerCardsInPlay(player, "Wolf-Skull Shaman");

        PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
        // Players would not choose to trigger Kinship ability if library is empty.
        // Useful for games when the "Milling = Loss Condition" check box is unchecked.

        if (kinship.size() == 0 || library.size() <= 0)
            return;

        final String[] shareTypes = {"Elf", "Shaman"};
        final Card[] prevCardShown = {null};
        final Card peek[] = {null};

        for (final Card k : kinship) {
            Ability ability = new Ability(k, "0") {    // change to triggered abilities when ready
                @Override
                public void resolve() {
                    PlayerZone library = AllZone.getZone(Constant.Zone.Library, player);
                    if (library.size() <= 0)
                        return;

                    peek[0] = library.get(0);
                    boolean wantToken = false;

                    // We assume that both players will want to peek, ask if they want to reveal.
                    // We do not want to slow down the pace of the game by asking too many questions.
                    // Dialogs outside of the Ability appear at the previous end of turn phase !!!

                    if (peek[0].isValidCard(shareTypes, k.getController(), k)) {
                        if (player.isHuman()) {
                            StringBuilder question = new StringBuilder();
                            question.append("Your top card is ").append(peek[0].getName());
                            question.append(". Reveal card and put a 2/2 green Wolf creature token onto the battlefield?");
                            if (showYesNoDialog(k, question.toString())) {
                                wantToken = true;
                            }
                        }
                        // player isComputer()
                        else {
                            String title = "Computer reveals";
                            revealTopCard(title);
                            wantToken = true;
                        }
                    } else if (player.isHuman()) {
                        String title = "Your top card is";
                        revealTopCard(title);
                    }

                    if (wantToken)
                        CardFactoryUtil.makeToken("Wolf", "G 2 2 Wolf", k.getController(), "G",
                                new String[]{"Creature", "Wolf"}, 2, 2, new String[]{""});
                }// resolve()

                private void revealTopCard(String title) {
                    if (peek[0] != prevCardShown[0]) {
                        GuiUtils.getChoice(title, peek[0]);
                        prevCardShown[0] = peek[0];
                    }
                }// revealTopCard()
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Wolf-Skull Shaman - ").append(player);
            sb.append(" triggers Kinship");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Wolf_Skull_Shaman()


    ///////////////////////
    // End of Kinship cards
    ///////////////////////


    /**
     * <p>upkeep_Dark_Confidant.</p>
     */
    private static void upkeep_Dark_Confidant() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player);
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.getName().equals("Dark Confidant") || c.getName().equals("Dark Tutelage");
            }
        });

        Ability ability;
        for (int i = 0; i < list.size(); i++) {
            final Card F_card = list.get(i);
            ability = new Ability(F_card, "0") {
                @Override
                public void resolve() {
                    CardList lib = AllZoneUtil.getPlayerCardsInLibrary(player);
                    if (lib.size() > 0) {
                        Card toMove = lib.get(0);
                        AllZone.getGameAction().moveToHand(toMove);
                        player.loseLife(toMove.getCMC(), F_card);
                    }
                }// resolve()
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append(F_card).append(" - ").append("At the beginning of your upkeep, reveal the top card of your library and put that card into your hand. You lose life equal to its converted mana cost.");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Dark_Confidant()

    


    /**
     * <p>upkeep_Suspend.</p>
     */
    public static void upkeep_Suspend() {
        Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInExile(player);

        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.hasSuspend();
            }
        });

        if (list.size() == 0) return;

        for (final Card c : list) {
            int counters = c.getCounters(Counters.TIME);
            if (counters > 0) c.subtractCounter(Counters.TIME, 1);
        }
    }//suspend	

    /**
     * <p>upkeep_Vanishing.</p>
     */
    private static void upkeep_Vanishing() {

        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player);
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return CardFactory.hasKeyword(c, "Vanishing") != -1;
            }
        });
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                final Card card = list.get(i);
                Ability ability = new Ability(card, "0") {
                    @Override
                    public void resolve() {
                        card.subtractCounter(Counters.TIME, 1);
                    }
                }; // ability

                StringBuilder sb = new StringBuilder();
                sb.append(card.getName()).append(" - Vanishing - remove a time counter from it. ");
                sb.append("When the last is removed, sacrifice it.)");
                ability.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }
    }

    /**
     * <p>upkeep_Fading.</p>
     */
    private static void upkeep_Fading() {

        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player);
        list = list.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return CardFactory.hasKeyword(c, "Fading") != -1;
            }
        });
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                final Card card = list.get(i);
                Ability ability = new Ability(card, "0") {
                    @Override
                    public void resolve() {
                        int fadeCounters = card.getCounters(Counters.FADE);
                        if (fadeCounters <= 0)
                            AllZone.getGameAction().sacrifice(card);
                        else
                            card.subtractCounter(Counters.FADE, 1);
                    }
                }; // ability

                StringBuilder sb = new StringBuilder();
                sb.append(card.getName()).append(" - Fading - remove a fade counter from it. ");
                sb.append("If you can't, sacrifice it.)");
                ability.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }
    }

    /**
     * <p>upkeep_Oath_of_Druids.</p>
     */
    private static void upkeep_Oath_of_Druids() {
        CardList oathList = AllZoneUtil.getCardsInPlay("Oath of Druids");
        if (oathList.isEmpty())
            return;

        final Player player = AllZone.getPhase().getPlayerTurn();

        if (AllZoneUtil.compareTypeAmountInPlay(player, "Creature") < 0) {
            for (int i = 0; i < oathList.size(); i++) {
                final Card oath = oathList.get(i);
                Ability ability = new Ability(oath, "0") {
                    @Override
                    public void resolve() {
                        CardList libraryList = AllZoneUtil.getPlayerCardsInLibrary(player);
                        PlayerZone battlefield = AllZone.getZone(Constant.Zone.Battlefield, player);
                        boolean oathFlag = true;

                        if (AllZoneUtil.compareTypeAmountInPlay(player, "Creature") < 0) {
                            if (player.isHuman()) {
                                StringBuilder question = new StringBuilder();
                                question.append("Reveal cards from the top of your library and place ");
                                question.append("the first creature revealed onto the battlefield?");
                                if (!GameActionUtil.showYesNoDialog(oath, question.toString())) {
                                    oathFlag = false;
                                }
                            } else {    // if player == Computer
                                CardList creaturesInLibrary = AllZoneUtil.getPlayerTypeInLibrary(player, "Creature");
                                CardList creaturesInBattlefield = AllZoneUtil.getPlayerTypeInPlay(player, "Creature");

                                // if there are at least 3 creatures in library, or none in play with one in library, oath
                                if (creaturesInLibrary.size() > 2
                                        || (creaturesInBattlefield.size() == 0 && creaturesInLibrary.size() > 0))
                                    oathFlag = true;
                                else
                                    oathFlag = false;
                            }

                            if (oathFlag) {
                                CardList cardsToReveal = new CardList();
                                int max = libraryList.size();
                                for (int i = 0; i < max; i++) {
                                    Card c = libraryList.get(i);
                                    cardsToReveal.add(c);
                                    if (c.isCreature()) {
                                        AllZone.getGameAction().moveTo(battlefield, c);
                                        break;
                                    } else {
                                        AllZone.getGameAction().moveToGraveyard(c);
                                    }
                                }// for loop
                                if (cardsToReveal.size() > 0)
                                    GuiUtils.getChoice("Revealed cards", cardsToReveal.toArray());
                            }
                        }
                    }
                };// Ability

                StringBuilder sb = new StringBuilder();
                sb.append("At the beginning of each player's upkeep, that player chooses target player ");
                sb.append("who controls more creatures than he or she does and is his or her opponent. The ");
                sb.append("first player may reveal cards from the top of his or her library until he or she ");
                sb.append("reveals a creature card. If he or she does, that player puts that card onto the ");
                sb.append("battlefield and all other cards revealed this way into his or her graveyard.");
                ability.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }
    }// upkeep_Oath of Druids()

    /**
     * <p>upkeep_Oath_of_Ghouls.</p>
     */
    private static void upkeep_Oath_of_Ghouls() {
        CardList oathList = AllZoneUtil.getCardsInPlay("Oath of Ghouls");
        if (oathList.isEmpty())
            return;

        final Player player = AllZone.getPhase().getPlayerTurn();

        if (AllZoneUtil.compareTypeAmountInGraveyard(player, "Creature") > 0) {
            for (int i = 0; i < oathList.size(); i++) {
                Ability ability = new Ability(oathList.get(0), "0") {
                    @Override
                    public void resolve() {
                        CardList graveyardCreatures = AllZoneUtil.getPlayerTypeInGraveyard(player, "Creature");

                        if (AllZoneUtil.compareTypeAmountInGraveyard(player, "Creature") > 0) {
                            if (player.isHuman()) {
                                Object o = GuiUtils.getChoiceOptional("Pick a creature to return to hand",
                                        graveyardCreatures.toArray());
                                if (o != null) {
                                    Card card = (Card) o;

                                    AllZone.getGameAction().moveToHand(card);
                                }
                            } else if (player.isComputer()) {
                                Card card = graveyardCreatures.get(0);

                                AllZone.getGameAction().moveToHand(card);
                            }
                        }
                    }
                };// Ability

                StringBuilder sb = new StringBuilder();
                sb.append("At the beginning of each player's upkeep, Oath of Ghouls returns a creature ");
                sb.append("from their graveyard to owner's hand if they have more than an opponent.");
                ability.setStackDescription(sb.toString());

                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }
    }//Oath of Ghouls


    /**
     * <p>upkeep_Karma.</p>
     */
    private static void upkeep_Karma() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList karmas = AllZoneUtil.getCardsInPlay("Karma");
        CardList swamps = AllZoneUtil.getPlayerTypeInPlay(player, "Swamp");

        // determine how much damage to deal the current player
        final int damage = swamps.size();

        // if there are 1 or more Karmas on the  
        // battlefield have each of them deal damage.
        if (0 < karmas.size()) {
            for (Card karma : karmas) {
                final Card src = karma;
                Ability ability = new Ability(src, "0") {
                    @Override
                    public void resolve() {
                        if (damage > 0) {
                            player.addDamage(damage, src);
                        }
                    }
                };// Ability
                if (damage > 0) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("Karma deals ").append(damage).append(" damage to ").append(player);
                    ability.setStackDescription(sb.toString());

                    AllZone.getStack().addSimultaneousStackEntry(ability);

                }
            }
        }// if
    }// upkeep_Karma()


    /**
     * <p>upkeep_Dega_Sanctuary.</p>
     */
    private static void upkeep_Dega_Sanctuary() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Dega Sanctuary");

        for (Card sanc : list) {
            final Card source = sanc;
            final Ability ability = new Ability(source, "0") {
                public void resolve() {
                    int gain = 0;
                    CardList play = AllZoneUtil.getPlayerCardsInPlay(player);
                    CardList black = play.filter(AllZoneUtil.black);
                    CardList red = play.filter(AllZoneUtil.red);
                    if (black.size() > 0 && red.size() > 0) gain = 4;
                    else if (black.size() > 0 || red.size() > 0) gain = 2;
                    player.gainLife(gain, source);
                }
            };//Ability

            StringBuilder sb = new StringBuilder();
            sb.append(source.getName()).append(" - ");
            sb.append("if you control a black or red permanent, you gain 2 life. If you control a black permanent and a red permanent, you gain 4 life instead.");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }//for
    }//upkeep_Dega_Sanctuary()

    /**
     * <p>upkeep_Ceta_Sanctuary.</p>
     */
    private static void upkeep_Ceta_Sanctuary() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Ceta Sanctuary");

        for (Card sanc : list) {
            final Card source = sanc;
            final Ability ability = new Ability(source, "0") {
                public void resolve() {
                    int draw = 0;
                    CardList play = AllZoneUtil.getPlayerCardsInPlay(player);
                    CardList green = play.filter(AllZoneUtil.green);
                    CardList red = play.filter(AllZoneUtil.red);

                    if (green.size() > 0 && red.size() > 0) draw = 2;
                    else if (green.size() > 0 || red.size() > 0) draw = 1;

                    if (draw > 0) {
                        player.drawCards(draw);
                        player.discard(1, this, true);
                    }
                }
            };//Ability

            StringBuilder sb = new StringBuilder();
            sb.append(source).append(" - ");
            sb.append("At the beginning of your upkeep, if you control a red or green permanent, draw a card, then discard a card. If you control a red permanent and a green permanent, instead draw two cards, then discard a card.");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }//for
    }//upkeep_Ceta_Sanctuary()

    /**
     * <p>upkeep_Power_Surge.</p>
     */
    private static void upkeep_Power_Surge() {
        /*
		 * At the beginning of each player's upkeep, Power Surge deals X
		 * damage to that player, where X is the number of untapped
		 * lands he or she controlled at the beginning of this turn.
		 */
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList list = AllZoneUtil.getCardsInPlay("Power Surge");
        final int damage = player.getNumPowerSurgeLands();

        for (Card surge : list) {
            final Card source = surge;
            Ability ability = new Ability(source, "0") {
                @Override
                public void resolve() {
                    player.addDamage(damage, source);
                }
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append(source).append(" - deals ").append(damage).append(" damage to ").append(player);
            ability.setStackDescription(sb.toString());

            if (damage > 0) {
                AllZone.getStack().addSimultaneousStackEntry(ability);

            }
        }// for
    }// upkeep_Power_Surge()

    /**
     * <p>upkeep_Felidar_Sovereign.</p>
     */
    private static void upkeep_Felidar_Sovereign() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Felidar Sovereign");

        if (0 < list.size() && player.getLife() >= 40) {
            final Card source = list.get(0);
            Ability ability = new Ability(source, "0") {
                @Override
                public void resolve() {
                    if (player.getLife() >= 40)
                        player.altWinConditionMet(source.getName());
                }
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append("Felidar Sovereign - ").append(player).append(" wins the game");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// if
    }// upkeep_Felidar_Sovereign

    
    /**
     * <p>upkeep_Mortal_Combat.</p>
     */
    private static void upkeep_Mortal_Combat() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Mortal Combat");
        CardList grave = AllZoneUtil.getPlayerGraveyard(player);
        grave = grave.filter(AllZoneUtil.creatures);

        if (0 < list.size() && 20 <= grave.size()) {
            final Card source = list.get(0);
            Ability ability = new Ability(source, "0") {
                @Override
                public void resolve() {
                    CardList grave = AllZoneUtil.getPlayerGraveyard(player);
                    grave = grave.filter(AllZoneUtil.creatures);
                    if (grave.size() >= 20)
                        player.altWinConditionMet(source.getName());
                }
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append("Mortal Combat - ").append(player).append(" wins the game");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// if
    }// upkeep_Mortal Combat

    /**
     * <p>upkeep_Helix_Pinnacle.</p>
     */
    private static void upkeep_Helix_Pinnacle() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Helix Pinnacle");

        for (final Card c : list) {
            if (c.getCounters(Counters.TOWER) < 100) continue;

            Ability ability = new Ability(c, "0") {
                @Override
                public void resolve() {
                    if (c.getCounters(Counters.TOWER) >= 100)
                        player.altWinConditionMet(c.getName());
                }
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append("Helix Pinnacle - ").append(player).append(" wins the game");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// if
    }// upkeep_Helix_Pinnacle

    /**
     * <p>upkeep_Near_Death_Experience.</p>
     */
    private static void upkeep_Near_Death_Experience() {
        /*
		 * At the beginning of your upkeep, if you have exactly 1 life, you win the game.
		 */
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Near-Death Experience");

        if (0 < list.size() && player.getLife() == 1) {
            final Card source = list.get(0);
            Ability ability = new Ability(source, "0") {
                @Override
                public void resolve() {
                    if (player.getLife() == 1)
                        player.altWinConditionMet(source.getName());
                }
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append("Near-Death Experience - ").append(player).append(" wins the game");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// if
    }// upkeep_Near_Death_Experience

    /**
     * <p>upkeep_Test_of_Endurance.</p>
     */
    private static void upkeep_Test_of_Endurance() {
        /*
		 * At the beginning of your upkeep, if you have 50 or more life, you win the game.
		 */
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Test of Endurance");

        if (0 < list.size() && player.getLife() >= 50) {
            final Card source = list.get(0);
            Ability ability = new Ability(source, "0") {
                @Override
                public void resolve() {
                    if (player.getLife() >= 50)
                        player.altWinConditionMet(source.getName());
                }
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append(list.get(0)).append(" - ").append(player).append(" wins the game");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// if
    }// upkeep_Test_of_Endurance


    /**
     * <p>upkeep_Barren_Glory.</p>
     */
    private static void upkeep_Barren_Glory() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        PlayerZone handZone = AllZone.getZone(Constant.Zone.Hand, player);

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player);
        CardList playList = AllZoneUtil.getPlayerCardsInPlay(player);
        playList = playList.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return !c.getName().equals("Mana Pool");
            }
        });

        list = list.getName("Barren Glory");

        if (playList.size() == 1 && list.size() == 1 && handZone.size() == 0) {
            final Card source = list.get(0);
            Ability ability = new Ability(source, "0") {
                @Override
                public void resolve() {
                    CardList handList = AllZoneUtil.getCardsInZone(Constant.Zone.Hand, player);
                    CardList playList = AllZoneUtil.getCardsInZone(Constant.Zone.Battlefield, player);
                    playList = playList.getValidCards("Permanents".split(","), source.getController(), source);
                    playList.remove(source);

                    if (playList.size() == 0 && handList.size() == 0)
                        player.altWinConditionMet(source.getName());
                }
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append("Barren Glory - ").append(player).append(" wins the game");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// if
    }// upkeep_Barren_Glory

    /**
     * <p>upkeep_Sleeper_Agent.</p>
     */
    private static void upkeep_Sleeper_Agent() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Sleeper Agent");

        Ability ability;
        for (int i = 0; i < list.size(); i++) {
            final Card F_card = list.get(i);
            ability = new Ability(list.get(i), "0") {
                @Override
                public void resolve() {
                    player.addDamage(2, F_card);
                }
            };

            ability.setStackDescription("Sleeper Agent deals 2 damage to its controller.");

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }
    }//upkeep_Sleeper_Agent

    /**
     * <p>upkeep_Shapeshifter.</p>
     */
    private static void upkeep_Shapeshifter() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Shapeshifter");
        list = list.filter(AllZoneUtil.nonToken);

        for (final Card c : list) {
            SpellAbility ability = new Ability(c, "0") {
                @Override
                public void resolve() {
                    int num = 0;
                    if (player.isHuman()) {
                        String[] choices = new String[7];
                        for (int j = 0; j < 7; j++) {
                            choices[j] = "" + j;
                        }
                        String answer = (String) (GuiUtils.getChoiceOptional(c.getName() + " - Choose a number", choices));
                        num = Integer.parseInt(answer);
                    } else {
                        num = 3;
                    }
                    c.setBaseAttack(num);
                    c.setBaseDefense(7 - num);
                }
            };
            ability.setStackDescription(c.getName() + " - choose a new number");

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }//foreach(Card)
    }//upkeep_Shapeshifter

    /**
     * <p>upkeep_Vesuvan_Doppelganger_Keyword.</p>
     */
    private static void upkeep_Vesuvan_Doppelganger_Keyword() {
        // TODO: what about enchantments? i dont know how great this solution is
        final Player player = AllZone.getPhase().getPlayerTurn();
        final String keyword = "At the beginning of your upkeep, you may have this creature become a copy of target creature except it doesn't copy that creature's color. If you do, this creature gains this ability.";
        CardList list = AllZoneUtil.getPlayerCardsInPlay(player);
        list = list.filter(AllZoneUtil.getKeywordFilter(keyword));

        for (final Card c : list) {
            final SpellAbility ability = new Ability(c, "0") {
                @Override
                public void resolve() {
                    final Card[] newTarget = new Card[1];
                    newTarget[0] = null;

                    final Ability switchTargets = new Ability(c, "0") {
                        public void resolve() {
                            if (newTarget[0] != null) {
                                /*
								 * 1. need to select new card - DONE
								 * 1a. need to create the newly copied card with pic and setinfo
								 * 2. need to add the leaves play command
								 * 3. need to transfer the keyword
								 * 4. need to update the clone origin of new card and old card
								 * 5. remove clone leaves play commands from old
								 * 5a. remove old from play
								 * 6. add new to play
								 */

                                Card newCopy = AllZone.getCardFactory().getCard(newTarget[0].getName(), player);
                                newCopy.setCurSetCode(newTarget[0].getCurSetCode());
                                newCopy.setImageFilename(newTarget[0].getImageFilename());

                                //need to add the leaves play command (2)
                                newCopy.addLeavesPlayCommand(c.getCloneLeavesPlayCommand());
                                c.removeTrigger(c.getCloneLeavesPlayCommand(), ZCTrigger.LEAVEFIELD);
                                newCopy.setCloneLeavesPlayCommand(c.getCloneLeavesPlayCommand());

                                newCopy.addExtrinsicKeyword(keyword);
                                newCopy.addColor("U", newCopy, false, true);
                                newCopy.setCloneOrigin(c.getCloneOrigin());
                                newCopy.getCloneOrigin().setCurrentlyCloningCard(newCopy);
                                c.setCloneOrigin(null);

                                //5
                                PlayerZone play = AllZone.getZone(c);
                                play.remove(c);

                                play.add(newCopy);
                            }
                        }
                    };

                    AllZone.getInputControl().setInput(new Input() {
                        private static final long serialVersionUID = 5662272658873063221L;

                        @Override
                        public void showMessage() {
                            AllZone.getDisplay().showMessage(c.getName() + " - Select new target creature.  (Click Cancel to remain as is.)");
                            ButtonUtil.enableOnlyCancel();
                        }

                        @Override
                        public void selectButtonCancel() {
                            stop();
                        }

                        @Override
                        public void selectCard(Card selectedCard, PlayerZone z) {
                            if (z.is(Constant.Zone.Battlefield) && selectedCard.isCreature()
                                    && CardFactoryUtil.canTarget(c, selectedCard)) {
                                newTarget[0] = selectedCard;
                                StringBuilder sb = new StringBuilder();
                                sb.append(c.getCloneOrigin()).append(" - switching to copy " + selectedCard.getName() + ".");
                                switchTargets.setStackDescription(sb.toString());
                                AllZone.getStack().add(switchTargets);
                                stop();
                            }
                        }
                    });
                }
            };
            ability.setDescription("At the beginning of your upkeep, you may have this creature become a copy of target creature except it doesn't copy that creature's color. If you do, this creature gains this ability.");
            ability.setStackDescription(c.getName() + " - you may have this creature become a copy of target creature.");

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }//foreach(Card)
    }//upkeep_Vesuvan_Doppelganger_Keyword

    /**
     * <p>upkeep_Tangle_Wire.</p>
     */
    private static void upkeep_Tangle_Wire() {
        final Player player = AllZone.getPhase().getPlayerTurn();
        CardList wires = AllZoneUtil.getCardsInPlay("Tangle Wire");

        for (final Card source : wires) {
            SpellAbility ability = new Ability(source, "0") {
                @Override
                public void resolve() {
                    final int num = source.getCounters(Counters.FADE);
                    final CardList list = AllZoneUtil.getPlayerCardsInPlay(player).filter(new CardListFilter() {
                        public boolean addCard(Card c) {
                            return (c.isArtifact() || c.isLand() || c.isCreature()) && c.isUntapped();
                        }
                    });

                    for (int i = 0; i < num; i++) {
                        if (player.isComputer()) {
                            Card toTap = CardFactoryUtil.AI_getWorstPermanent(list, false, false, false, false);
                            if (null != toTap) {
                                toTap.tap();
                                list.remove(toTap);
                            }
                        } else {
                            AllZone.getInputControl().setInput(new Input() {
                                private static final long serialVersionUID = 5313424586016061612L;

                                public void showMessage() {
                                    if (list.size() == 0) {
                                        stop();
                                        return;
                                    }
                                    AllZone.getDisplay().showMessage(source.getName() + " - Select " + num + " untapped artifact(s), creature(s), or land(s) you control");
                                    ButtonUtil.disableAll();
                                }

                                public void selectCard(Card card, PlayerZone zone) {
                                    if (zone.is(Constant.Zone.Battlefield, AllZone.getHumanPlayer()) && list.contains(card)) {
                                        card.tap();
                                        list.remove(card);
                                        stop();
                                    }
                                }
                            });
                        }
                    }
                }
            };
            ability.setStackDescription(source.getName() + " - " + player + " taps X artifacts, creatures or lands he or she controls.");

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }//foreach(wire)
    }//upkeep_Tangle_Wire()

    

    /**
     * <p>upkeep_Masticore.</p>
     */
    private static void upkeep_Masticore() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Masticore");
        list.addAll(AllZoneUtil.getPlayerCardsInPlay(player, "Molten-Tail Masticore"));
        list.addAll(AllZoneUtil.getPlayerCardsInPlay(player, "Razormane Masticore"));

        Ability ability;
        for (int i = 0; i < list.size(); i++) {
            final Card crd = list.get(i);

            final Input discard = new Input() {
                private static final long serialVersionUID = 2252076866782738069L;

                @Override
                public void showMessage() {
                    AllZone.getDisplay().showMessage(crd + " - Discard a card from your hand");
                    ButtonUtil.enableOnlyCancel();
                }

                @Override
                public void selectCard(Card c, PlayerZone zone) {
                    if (zone.is(Constant.Zone.Hand)) {
                        c.getController().discard(c, null);
                        stop();
                    }
                }

                @Override
                public void selectButtonCancel() {
                    AllZone.getGameAction().sacrifice(crd);
                    stop();
                }
            };//Input

            ability = new Ability(crd, "0") {
                @Override
                public void resolve() {
                    if (crd.getController().isHuman()) {
                        if (AllZone.getHumanHand().size() == 0) AllZone.getGameAction().sacrifice(crd);
                        else AllZone.getInputControl().setInput(discard);
                    } else //comp
                    {
                        CardList list = AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer());

                        if (list.size() != 0) list.get(0).getController().discard(list.get(0), this);
                        else AllZone.getGameAction().sacrifice(crd);
                    }//else
                }//resolve()
            };//Ability

            StringBuilder sb = new StringBuilder();
            sb.append(crd).append(" - sacrifice ").append(crd).append(" unless you discard a card.");
            ability.setStackDescription(sb.toString());
            ability.setDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }//upkeep_Masticore


    /**
     * <p>upkeep_Eldrazi_Monument.</p>
     */
    private static void upkeep_Eldrazi_Monument() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Eldrazi Monument");

        Ability ability;
        for (int i = 0; i < list.size(); i++) {
            final Card card = list.get(i);
            ability = new Ability(list.get(i), "0") {
                @Override
                public void resolve() {
                    CardList creats = AllZoneUtil.getCreaturesInPlay(player);

                    if (creats.size() < 1) {
                        AllZone.getGameAction().sacrifice(card);
                        return;
                    }

                    if (player.isHuman()) {
                        Object o = GuiUtils.getChoiceOptional("Select creature to sacrifice",
                                creats.toArray());
                        Card sac = (Card) o;
                        if (sac == null) {
                            creats.shuffle();
                            sac = creats.get(0);
                        }
                        AllZone.getGameAction().sacrifice(sac);
                    } else//computer
                    {
                        CardListUtil.sortAttackLowFirst(creats);
                        AllZone.getGameAction().sacrifice(creats.get(0));
                    }
                }
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append("Eldrazi Monument - ").append(player).append(" sacrifices a creature.");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }

    }//upkeep_Eldrazi_Monument

    /**
     * <p>upkeep_Blaze_Counters.</p>
     */
    private static void upkeep_Blaze_Counters() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList blaze = AllZoneUtil.getPlayerCardsInPlay(player);
        blaze = blaze.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.isLand() && c.getCounters(Counters.BLAZE) > 0;
            }
        });

        for (int i = 0; i < blaze.size(); i++) {
            final Card Source = blaze.get(i);
            Ability ability = new Ability(blaze.get(i), "0") {
                @Override
                public void resolve() {
                    player.addDamage(1, Source);
                }
            };// ability

            StringBuilder sb = new StringBuilder();
            sb.append(blaze.get(i)).append(" - has a blaze counter and deals 1 damage to ");
            sb.append(player).append(".");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }
    }

    /**
     * <p>upkeep_Dragon_Broodmother.</p>
     */
    /*
    private static void upkeep_Dragon_Broodmother() {
        CardList list = AllZoneUtil.getCardsInPlay("Dragon Broodmother");

        Ability ability;
        for (int i = 0; i < list.size(); i++) {
            final Card card = list.get(i);
            ability = new Ability(card, "0") {
                @Override
                public void resolve() {
                    int multiplier = AllZoneUtil.getDoublingSeasonMagnitude(card.getController());
                    for (int i = 0; i < multiplier; i++)
                        makeToken();

                }// resolve()

                public void makeToken() {
                    //CardList cl = CardFactoryUtil.makeToken("Dragon", "RG 1 1 Dragon", card, "RG", new String[] {"Creature", "Dragon"}, 1, 1, new String[] {"Flying"} );


                    final Card c = new Card();

                    c.setOwner(card.getController());
                    c.setController(card.getController());

                    c.setName("Dragon");
                    c.setImageName("RG 1 1 Dragon");
                    //c.setManaCost("RG");
                    c.addColor("RG");
                    c.setToken(true);

                    c.addType("Creature");
                    c.addType("Dragon");

                    c.addIntrinsicKeyword("Flying");

                    c.setBaseAttack(1);
                    c.setBaseDefense(1);

                    //final String player = card.getController();
                    final int[] numCreatures = new int[1];

                    final SpellAbility devour = new Spell(card) {

                        private static final long serialVersionUID = 4158780345303896275L;

                        @Override
                        public void resolve() {
                            int totalCounters = numCreatures[0] * 2;
                            c.addCounter(Counters.P1P1, totalCounters);

                        }

                        @Override
                        public boolean canPlay() {
                            return AllZone.getPhase().getPlayerTurn().equals(card.getController())
                                    && card.isFaceDown() && !AllZone.getPhase().getPhase().equals("End of Turn")
                                    && AllZoneUtil.isCardInPlay(card);
                        }

                    };//devour

                    Command intoPlay = new Command() {

                        private static final long serialVersionUID = -9220268793346809216L;

                        public void execute() {

                            CardList creatsToSac = new CardList();
                            CardList creats = AllZoneUtil.getPlayerCardsInPlay(card.getController());
                            creats = creats.filter(new CardListFilter() {
                                public boolean addCard(Card crd) {
                                    return crd.isCreature() && !crd.equals(c);
                                }
                            });

                            //System.out.println("Creats size: " + creats.size());

                            if (card.getController().isHuman()) {
                                Object o = null;
                                int creatsSize = creats.size();

                                for (int k = 0; k < creatsSize; k++) {
                                    o = GuiUtils.getChoiceOptional("Select creature to sacrifice",
                                            creats.toArray());

                                    if (o == null) break;

                                    Card crd = (Card) o;
                                    creatsToSac.add(crd);
                                    creats.remove(crd);
                                }

                                numCreatures[0] = creatsToSac.size();
                                for (int m = 0; m < creatsToSac.size(); m++) {
                                    AllZone.getGameAction().sacrifice(creatsToSac.get(m));
                                }

                            }//human
                            else {
                                int count = 0;
                                for (int i = 0; i < creats.size(); i++) {
                                    Card crd = creats.get(i);
                                    if (crd.getNetAttack() <= 1 && crd.getNetDefense() <= 2) {
                                        AllZone.getGameAction().sacrifice(crd);
                                        count++;
                                    }
                                }
                                numCreatures[0] = count;
                            }
                            AllZone.getStack().addSimultaneousStackEntry(devour);

                        }
                    };

                    StringBuilder sb = new StringBuilder();
                    sb.append(c.getName()).append(" - gets 2 +1/+1 counter(s) per devoured creature.");
                    devour.setStackDescription(sb.toString());

                    devour.setDescription("Devour 2");
                    c.addSpellAbility(devour);
                    c.addComesIntoPlayCommand(intoPlay);

                    AllZone.getGameAction().moveToPlay(c);
                }
            };// Ability
            ability.setStackDescription("Dragon Broodmother - put a 1/1 red and green Dragon token onto the battlefield.");

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }// for
    }// upkeep_Dragon_Broodmother()
*/
    /**
     * <p>draw_Sylvan_Library.</p>
     *
     * @param player a {@link forge.Player} object.
     */
    private static void draw_Sylvan_Library(final Player player) {
        /*
		 * At the beginning of your draw step, you may draw two additional
		 * cards. If you do, choose two cards in your hand drawn this turn.
		 * For each of those cards, pay 4 life or put the card on top of
		 * your library.
		 */
        final CardList cards = AllZoneUtil.getPlayerCardsInPlay(player, "Sylvan Library");

        for (final Card source : cards) {
            final Ability ability = new Ability(source, "") {
                @Override
                public void resolve() {
                    final Player player = source.getController();
                    if (player.isHuman()) {
                        String question = "Draw 2 additional cards?";
                        final String cardQuestion = "Pay 4 life and keep in hand?";
                        if (GameActionUtil.showYesNoDialog(source, question)) {
                            player.drawCards(2);
                            for (int i = 0; i < 2; i++) {
                                final String prompt = source + " - Select a card drawn this turn: " + (2 - i) + " of 2";
                                AllZone.getInputControl().setInput(new Input() {
                                    private static final long serialVersionUID = -3389565833121544797L;

                                    @Override
                                    public void showMessage() {
                                        if (AllZone.getHumanHand().size() == 0) stop();
                                        AllZone.getDisplay().showMessage(prompt);
                                        ButtonUtil.disableAll();
                                    }

                                    @Override
                                    public void selectCard(Card card, PlayerZone zone) {
                                        if (zone.is(Constant.Zone.Hand) && true == card.getDrawnThisTurn()) {
                                            if (player.canPayLife(4) && GameActionUtil.showYesNoDialog(source, cardQuestion)) {
                                                player.payLife(4, source);
                                                //card stays in hand
                                            } else {
                                                AllZone.getGameAction().moveToLibrary(card);
                                            }
                                            stop();
                                        }
                                    }
                                });//end Input
                            }
                        }
                    } else {
                        //Computer, but he's too stupid to play this
                    }
                }//resolve
            };// Ability

            StringBuilder sb = new StringBuilder();
            sb.append("At the beginning of your draw step, you may draw two additional cards. If you do, choose two cards in your hand drawn this turn. For each of those cards, pay 4 life or put the card on top of your library.");
            ability.setStackDescription(sb.toString());

            AllZone.getStack().addSimultaneousStackEntry(ability);

        }//end for
    }

    /**
     * <p>upkeep_Carnophage.</p>
     */
    private static void upkeep_Carnophage() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Carnophage");
        if (player.isHuman()) {
            for (int i = 0; i < list.size(); i++) {
                Card c = list.get(i);
                String[] choices = {"Yes", "No"};
                Object choice = GuiUtils.getChoice("Pay Carnophage's upkeep?", choices);
                if (choice.equals("Yes")) player.loseLife(1, c);
                else c.tap();
            }
        } else if (player.isComputer()) for (int i = 0; i < list.size(); i++) {
            Card c = list.get(i);
            if (AllZone.getComputerPlayer().getLife() > 1) player.loseLife(1, c);
            else c.tap();
        }
    }// upkeep_Carnophage

    /**
     * <p>upkeep_Sangrophage.</p>
     */
    private static void upkeep_Sangrophage() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList list = AllZoneUtil.getPlayerCardsInPlay(player, "Sangrophage");
        if (player.isHuman()) {
            for (int i = 0; i < list.size(); i++) {
                Card c = list.get(i);
                String[] choices = {"Yes", "No"};
                Object choice = GuiUtils.getChoice("Pay Sangrophage's upkeep?", choices);
                if (choice.equals("Yes")) player.loseLife(2, c);
                else c.tap();
            }
        } else if (player.isComputer()) for (int i = 0; i < list.size(); i++) {
            Card c = list.get(i);
            if (AllZone.getComputerPlayer().getLife() > 2) player.loseLife(2, c);
            else c.tap();
        }
    }// upkeep_Carnophage

    /**
     * <p>upkeep_Fallen_Empires_Storage_Lands.</p>
     */
    private static void upkeep_Fallen_Empires_Storage_Lands() {
        final Player player = AllZone.getPhase().getPlayerTurn();

        CardList all = AllZoneUtil.getPlayerCardsInPlay(player, "Bottomless Vault");
        all.addAll(AllZoneUtil.getPlayerCardsInPlay(player, "Dwarven Hold"));
        all.addAll(AllZoneUtil.getPlayerCardsInPlay(player, "Hollow Trees"));
        all.addAll(AllZoneUtil.getPlayerCardsInPlay(player, "Icatian Store"));
        all.addAll(AllZoneUtil.getPlayerCardsInPlay(player, "Sand Silos"));

        for (Card land : all) {
            if (land.isTapped()) land.addCounter(Counters.STORAGE, 1);
        }
    } //upkeep_Fallen_Empires_Storage_Lands

    /** Constant <code>Conspiracy</code> */
    public static Command Conspiracy = new Command() {
        private static final long serialVersionUID = -752798545956593342L;

        CardList gloriousAnthemList = new CardList();

        public void execute() {
            //String keyword = "Defender";

            CardList list = gloriousAnthemList;
            Card c;
            // reset all cards in list - aka "old" cards
            for (int i = 0; i < list.size(); i++) {
                c = list.get(i);
                //System.out.println("prev type: " +c.getPrevType());
                c.setType(c.getPrevType());
            }

            list.clear();

            PlayerZone[] zone = new PlayerZone[4];

            CardList cl = AllZoneUtil.getCardsInPlay("Conspiracy");

            for (int i = 0; i < cl.size(); i++) {
                Card card = cl.get(i);
                Player player = card.getController();
                zone[0] = AllZone.getZone(Constant.Zone.Hand,
                        player);
                zone[1] = AllZone.getZone(Constant.Zone.Library,
                        player);
                zone[2] = AllZone.getZone(
                        Constant.Zone.Graveyard, player);
                zone[3] = AllZone.getZone(Constant.Zone.Battlefield,
                        player);

                for (int outer = 0; outer < zone.length; outer++) {
                    CardList creature = AllZoneUtil.getCardsInZone(zone[outer]);
                    creature = creature.getType("Creature");

                    for (int j = 0; j < creature.size(); j++) {
                        boolean art = false;
                        boolean ench = false;

                        c = creature.get(j);

                        if (c.isArtifact()) art = true;
                        if (c.isEnchantment()) ench = true;

                        if (c.getPrevType().size() == 0) c.setPrevType(c.getType());
                        c.setType(new ArrayList<String>());
                        c.addType("Creature");
                        if (art) c.addType("Artifact");
                        if (ench) c.addType("Enchantment");
                        c.addType(card.getChosenType());

                        gloriousAnthemList.add(c);
                    }
                }
            }// for inner
        }// execute()
    }; //Conspiracy


    /** Constant <code>Elspeth_Emblem</code> */
    public static Command Elspeth_Emblem = new Command() {

        private static final long serialVersionUID = 7414127991531889390L;
        CardList gloriousAnthemList = new CardList();

        public void execute() {
            String keyword = "Indestructible";

            CardList list = gloriousAnthemList;
            Card c;
            // reset all cards in list - aka "old" cards
            for (int i = 0; i < list.size(); i++) {
                c = list.get(i);
                c.removeExtrinsicKeyword(keyword);
            }

            list.clear();

            CardList emblem = AllZoneUtil.getCardsInPlay();
            emblem = emblem.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return c.isEmblem()
                            && c.hasKeyword("Artifacts, creatures, enchantments, and lands you control are indestructible.");
                }
            });

            for (int i = 0; i < emblem.size(); i++) {
                CardList perms = AllZoneUtil.getPlayerCardsInPlay(emblem.get(i).getController());

                for (int j = 0; j < perms.size(); j++) {
                    c = perms.get(j);
                    if (!c.hasKeyword(keyword)) {
                        c.addExtrinsicKeyword(keyword);
                        gloriousAnthemList.add(c);
                    }
                }
            }
        }// execute()
    };

    /** Constant <code>Favor_of_the_Mighty</code> */
    public static Command Favor_of_the_Mighty = new Command() {
        private static final long serialVersionUID = 2920036758177137722L;
        private CardList pumped = new CardList();

        public void execute() {
            //Reset old cards
            for (Card c : pumped) {
                c.removeIntrinsicKeyword("Protection from white");
                c.removeIntrinsicKeyword("Protection from blue");
                c.removeIntrinsicKeyword("Protection from black");
                c.removeIntrinsicKeyword("Protection from red");
                c.removeIntrinsicKeyword("Protection from green");
            }
            pumped.clear();

            //Find creature(s) with highest cmc
            int maxCMC = -1;
            //boolean keepLooping = true;
            CardList creats = AllZoneUtil.getCreaturesInPlay();
            for (Card c : creats) {
                if (c.getCMC() > maxCMC) {
                    pumped.clear();
                    pumped.add(c);
                    maxCMC = c.getCMC();
                } else if (c.getCMC() == maxCMC) {
                    pumped.add(c);
                }
            }

            //Pump new cards
            for (Card c : pumped) {
                c.addIntrinsicKeyword("Protection from white");
                c.addIntrinsicKeyword("Protection from blue");
                c.addIntrinsicKeyword("Protection from black");
                c.addIntrinsicKeyword("Protection from red");
                c.addIntrinsicKeyword("Protection from green");
            }
        }
    };

    /** Constant <code>Koth_Emblem</code> */
    public static Command Koth_Emblem = new Command() {

        private static final long serialVersionUID = -3233715310427996429L;
        CardList gloriousAnthemList = new CardList();

        public void execute() {
            CardList list = gloriousAnthemList;
            Card crd;
            // reset all cards in list - aka "old" cards
            for (int i = 0; i < list.size(); i++) {
                crd = list.get(i);
                SpellAbility[] sas = crd.getSpellAbility();
                for (int j = 0; j < sas.length; j++) {
                    if (sas[j].isKothThirdAbility())
                        crd.removeSpellAbility(sas[j]);
                }
            }

            CardList emblem = AllZoneUtil.getCardsInPlay();
            emblem = emblem.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return c.isEmblem()
                            && c.hasKeyword("Mountains you control have 'tap: This land deals 1 damage to target creature or player.'");
                }
            });

            for (int i = 0; i < emblem.size(); i++) {
                CardList mountains = AllZoneUtil.getPlayerCardsInPlay(emblem.get(i).getController());
                mountains = mountains.filter(new CardListFilter() {
                    public boolean addCard(Card crd) {
                        return crd.isType("Mountain");
                    }
                });

                for (int j = 0; j < mountains.size(); j++) {
                    final Card c = mountains.get(j);
                    boolean hasAbility = false;
                    SpellAbility[] sas = c.getSpellAbility();
                    for (SpellAbility sa : sas) {
                        if (sa.isKothThirdAbility())
                            hasAbility = true;
                    }

                    if (!hasAbility) {
                        Cost abCost = new Cost("T", c.getName(), true);
                        Target target = new Target(c, "TgtCP");
                        final Ability_Activated ability = new Ability_Activated(c, abCost, target) {
                            private static final long serialVersionUID = -7560349014757367722L;

                            public void chooseTargetAI() {
                                CardList list = CardFactoryUtil.AI_getHumanCreature(1, c, true);
                                list.shuffle();

                                if (list.isEmpty() || AllZone.getHumanPlayer().getLife() < 5)
                                    setTargetPlayer(AllZone.getHumanPlayer());
                                else
                                    setTargetCard(list.get(0));
                            }

                            public void resolve() {
                                if (getTargetCard() != null) {
                                    if (AllZoneUtil.isCardInPlay(getTargetCard()) && CardFactoryUtil.canTarget(c, getTargetCard()))
                                        getTargetCard().addDamage(1, c);
                                } else {
                                    getTargetPlayer().addDamage(1, c);
                                }
                            }//resolve()
                        };//SpellAbility
                        ability.setKothThirdAbility(true);
                        ability.setDescription(abCost + "This land deals 1 damage to target creature or player.");

                        c.addSpellAbility(ability);

                        gloriousAnthemList.add(c);
                    }
                }
            }

        }
    };

    /** Constant <code>stPump</code> */
    public static Command stPump = new Command() {
        /** StaticEffectKeyword
         * Syntax:[ k[0] stPump[All][Self][Other] : k[1] Which Cards the Bonus Affects : 
         * 			k[2] What Bonus does the Card have : k[3] Special Conditions : k[4] Description
         */

        private static final long serialVersionUID = -7853346190458174501L;
        private ArrayList<StaticEffect> storage = new ArrayList<StaticEffect>();
        // storage stores the source card and the cards it gave its bonus to, to know what to remove

        public void execute() {

            // remove all static effects
            for (int i = 0; i < storage.size(); i++) {
                removeStaticEffect(storage.get(i));
            }

            //clear the list
            storage = new ArrayList<StaticEffect>();

            //Gather Cards on the Battlefield with the stPump Keyword
            CardList cards_WithKeyword = AllZoneUtil.getCardsInPlay();
            cards_WithKeyword = cards_WithKeyword.getKeywordsContain("stPump");

            // check each card
            for (int i = 0; i < cards_WithKeyword.size(); i++) {
                Card cardWithKeyword = cards_WithKeyword.get(i);
                ArrayList<String> keywords = cardWithKeyword.getKeyword();

                // check each keyword of the card
                for (int j = 0; j < keywords.size(); j++) {
                    String keyword = keywords.get(j);

                    if (keyword.startsWith("stPump")) {
                    	
                        StaticEffect se = new StaticEffect();     //create a new StaticEffect
                        se.setSource(cardWithKeyword);
                        se.setKeywordNumber(j);


                        //get the affected cards
                        String k[] = keyword.split(":", 5);

                        if (specialConditionsMet(cardWithKeyword, k[3])) { //special Conditions are Threshold, etc.

                            final String affected = k[1];
                            final String specific[] = affected.split(",");
                            CardList affectedCards = AffectedCards(cardWithKeyword, k); // options are All, Self, Enchanted etc.
                            affectedCards = affectedCards.getValidCards(specific, cardWithKeyword.getController(), cardWithKeyword);
                            se.setAffectedCards(affectedCards);

                            String[] pt = k[2].split("/");

                            Card cardWithXValue;
                            String xString = cardWithKeyword.getSVar("X").split("$")[0];
                            Card cardWithYValue;
                            String yString = cardWithKeyword.getSVar("Y").split("$")[0];

                            if (xString.startsWith("Imprinted") && !cardWithKeyword.getImprinted().isEmpty()) {
                                cardWithXValue = cardWithKeyword.getImprinted().get(0);
                            } else cardWithXValue = cardWithKeyword;

                            if (yString.startsWith("Imprinted") && !cardWithKeyword.getImprinted().isEmpty()) {
                                cardWithYValue = cardWithKeyword.getImprinted().get(0);
                            } else cardWithYValue = cardWithKeyword;

                            int x = 0;
                            if (pt[0].contains("X") || pt[1].contains("X"))
                                x = CardFactoryUtil.xCount(cardWithXValue, cardWithKeyword.getSVar("X").split("\\$")[1]);
                            se.setXValue(x);

                            int y = 0;
                            if (pt[1].contains("Y"))
                                y = CardFactoryUtil.xCount(cardWithYValue, cardWithKeyword.getSVar("Y").split("\\$")[1]);
                            se.setYValue(y);

                            addStaticEffects(cardWithKeyword, affectedCards, k[2], x, y); //give the boni to the affected cards

                            storage.add(se); // store the information
                        }
                    }
                }
            }
        }// execute()

        void addStaticEffects(Card source, CardList affectedCards, String Keyword_Details, int xValue, int yValue) {

            int powerbonus = 0;
            int toughnessbonus = 0;
            String[] Keyword = Keyword_Details.split("/", 3);

            Keyword[0] = Keyword[0].replace("+", "");
            Keyword[1] = Keyword[1].replace("+", "");

            if (!Keyword[0].contains("X")) powerbonus = Integer.valueOf(Keyword[0]);
            else powerbonus = xValue;         // the xCount takes places before

            if (Keyword[1].contains("X")) toughnessbonus = xValue;
            else if (Keyword[1].contains("Y")) toughnessbonus = yValue;
            else toughnessbonus = Integer.valueOf(Keyword[1]);

            for (int i = 0; i < affectedCards.size(); i++) {
                Card affectedCard = affectedCards.get(i);
                affectedCard.addSemiPermanentAttackBoost(powerbonus);
                affectedCard.addSemiPermanentDefenseBoost(toughnessbonus);
                if (Keyword.length > 2) {
                    String Keywords[] = Keyword[2].split(" & ");
                    for (int j = 0; j < Keywords.length; j++) {
                        String keyword = Keywords[j];
                        if (keyword.startsWith("SVar=")) {
                            String sVar = source.getSVar(keyword.split("SVar=")[1]);
                            if (sVar.startsWith("AB")) { // grant the ability
                                AbilityFactory AF = new AbilityFactory();
                                SpellAbility sa = AF.getAbility(sVar, affectedCard);
                                sa.setType("Temporary");

                                affectedCard.addSpellAbility(sa);
                            }
							else if (sVar.startsWith("Mode")){ // grant a Trigger
								StringBuilder triggerName = new StringBuilder("From ");
                                triggerName.append(source.getName()).append(" (").append(source.getUniqueNumber()).append("): ").append(keyword.split("SVar=")[1]);

                                Trigger actualTrigger = forge.card.trigger.TriggerHandler.parseTrigger(triggerName.toString(),sVar,affectedCard);
                                affectedCard.addTrigger(actualTrigger);
                                AllZone.getTriggerHandler().registerTrigger(actualTrigger);
							} else { // Copy this SVar
                                affectedCard.setSVar(keyword.split("SVar=")[1], sVar);
                            }
                        } else if (keyword.startsWith("Types=")) {
                            String[] tmptypes = keyword.split("=");
                            String[] types = tmptypes[1].split(",");
                            if (types[0].equals("ChosenType")) {
                                types[0] = source.getChosenType();
                            }
                            for (String type : types) affectedCard.addType(type);
                        } else if (keyword.startsWith("Keyword=")) {
                            String sVar = source.getSVar(keyword.split("Keyword=")[1]);
                            affectedCard.addExtrinsicKeyword(sVar);
                        } else if (keyword.startsWith("ForceSVar=")) {
                            String sVar = source.getSVar(keyword.split("SVar=")[1]);
                            affectedCard.setSVar(keyword.split("SVar=")[1],sVar);
                        } else affectedCard.addExtrinsicKeyword(keyword);
                    }
                }
            }
        }

        void removeStaticEffect(StaticEffect se) {
            Card source = se.getSource();
            CardList affected = se.getAffectedCards();
            int KeywordNumber = se.getKeywordNumber();
            int xValue = se.getXValue();         // the old xValue has to be removed, not the actual one!
            int yValue = se.getYValue();         // the old xValue has to be removed, not the actual one!
            String parse = source.getKeyword().get(KeywordNumber).toString();
            String k[] = parse.split(":");
            for (int i = 0; i < affected.size(); i++) {
                removeStaticEffect(source, affected.get(i), k, xValue, yValue);
            }
        }

        void removeStaticEffect(Card source, Card affectedCard, String[] Keyword_Details, int xValue, int yValue) {

            int powerbonus = 0;
            int toughnessbonus = 0;
            String[] Keyword = Keyword_Details[2].split("/", 3);

            Keyword[0] = Keyword[0].replace("+", "");
            Keyword[1] = Keyword[1].replace("+", "");

            if (!Keyword[0].contains("X")) powerbonus = Integer.valueOf(Keyword[0]);
            else powerbonus = xValue;

            if (Keyword[1].contains("X")) toughnessbonus = xValue;
            else if (Keyword[1].contains("Y")) toughnessbonus = yValue;
            else toughnessbonus = Integer.valueOf(Keyword[1]);

            affectedCard.addSemiPermanentAttackBoost(powerbonus * -1);
            affectedCard.addSemiPermanentDefenseBoost(toughnessbonus * -1);
            if (Keyword.length > 2) {
                String Keywords[] = Keyword[2].split(" & ");
                for (int j = 0; j < Keywords.length; j++) {
                    String keyword = Keywords[j];
                    if (keyword.startsWith("SVar=")) {
                        String sVar = source.getSVar(keyword.split("SVar=")[1]);
                        if (sVar.startsWith("AB")) { // remove granted abilities
                            SpellAbility[] spellAbility = affectedCard.getSpellAbility();
                            for (SpellAbility s : spellAbility) {
                                if (s.getType().equals("Temporary"))
                                    affectedCard.removeSpellAbility(s);
                            }
                        }
                        else if (sVar.startsWith("Mode")) { // remove granted triggers
                            StringBuilder triggerName = new StringBuilder("From ");
                            triggerName.append(source.getName()).append(" (").append(source.getUniqueNumber()).append("): ").append(keyword.split("SVar=")[1]);

                            Trigger actualTrigger = affectedCard.getNamedTrigger(triggerName.toString());
                            affectedCard.removeTrigger(actualTrigger);
                            AllZone.getTriggerHandler().removeRegisteredTrigger(actualTrigger);
                        }
                    } else if (keyword.startsWith("Types=")) {
                        String[] tmptypes = keyword.split("=");
                        String[] types = tmptypes[1].split(",");
                        if (types[0].equals("ChosenType")) {
                            types[0] = source.getChosenType();
                        }
                        for (String type : types) affectedCard.removeType(type);
                    } else if (keyword.startsWith("Keyword=")) {
                        String sVar = source.getSVar(keyword.split("Keyword=")[1]);
                        affectedCard.removeExtrinsicKeyword(sVar);
                    }
                    affectedCard.removeExtrinsicKeyword(keyword);
                }
            }
        }

        CardList AffectedCards(Card SourceCard, String[] Keyword_Details) {
            // [Self], [All], [Other]
            CardList Cards_inZone = new CardList();
            String Range = Keyword_Details[0];

            if (Range.contains("Self")) {
                Cards_inZone.add(SourceCard);
            }
            if (Range.contains("All")) {
                Cards_inZone.addAll(AllZoneUtil.getCardsInPlay());
                //this is a hack for Quick Sliver
                if (Keyword_Details.length >= 2
                        && (Keyword_Details[2].contains("Flash")
                        || Keyword_Details[2].contains("CARDNAME can't be countered."))) {
                    Cards_inZone.addAll(AllZoneUtil.getPlayerHand(AllZone.getHumanPlayer()));
                    Cards_inZone.addAll(AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer()));
                    Cards_inZone.addAll(AllZoneUtil.getCardsInGraveyard());
                }
                //hack for Molten Disaster
                /*
      			 // TODO for future use
      			if(Keyword_Details.length >= 2 && Keyword_Details[2].contains("Split second")) {
      				Cards_inZone.add(AllZone.getStack().getSpellCardsOnStack());
      			}
      			*/
            }
            if (Range.contains("Enchanted")) {
                if (SourceCard.getEnchanting().size() > 0)
                    Cards_inZone.addAll(SourceCard.getEnchanting().toArray());
            }

            if (Range.contains("Equipped")) {
                if (SourceCard.getEquipping().size() > 0)
                    Cards_inZone.addAll(SourceCard.getEquipping().toArray());
            }

            return Cards_inZone;
        }
    };

    // Special Conditions
    /**
     * <p>specialConditionsMet.</p>
     *
     * @param SourceCard a {@link forge.Card} object.
     * @param SpecialConditions a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean specialConditionsMet(Card SourceCard, String SpecialConditions) {

        if (SpecialConditions.contains("CardsInHandMore")) {
            CardList SpecialConditionsCardList = AllZoneUtil.getPlayerHand(SourceCard.getController());
            String Condition = SpecialConditions.split("/")[1];
            if (SpecialConditionsCardList.size() < Integer.valueOf(Condition)) return false;
        }
        if (SpecialConditions.contains("OppHandEmpty")) {
            CardList oppHand = AllZoneUtil.getPlayerHand(SourceCard.getController().getOpponent());
            if (!(oppHand.size() == 0)) return false;
        }
        if (SpecialConditions.contains("TopCardOfLibraryIsBlack")) {
            PlayerZone lib = AllZone.getZone(Constant.Zone.Library, SourceCard.getController());
            if (!(lib.get(0).isBlack())) return false;
        }
        if (SpecialConditions.contains("LibraryLE")) {
            CardList Library = AllZoneUtil.getPlayerCardsInLibrary(SourceCard.getController());
            String maxnumber = SpecialConditions.split("/")[1];
            if (Library.size() > Integer.valueOf(maxnumber)) return false;
        }
        if (SpecialConditions.contains("LifeGE")) {
            int life = SourceCard.getController().getLife();
            String maxnumber = SpecialConditions.split("/")[1];
            if (!(life >= Integer.valueOf(maxnumber))) return false;
        }
        if (SpecialConditions.contains("OppCreatureInPlayGE")) {
            CardList OppInPlay = AllZoneUtil.getPlayerCardsInPlay(SourceCard.getController().getOpponent());
            OppInPlay = OppInPlay.getType("Creature");
            String maxnumber = SpecialConditions.split("/")[1];
            if (!(OppInPlay.size() >= Integer.valueOf(maxnumber))) return false;
        }
        if (SpecialConditions.contains("LandYouCtrlLE")) {
            CardList LandInPlay = AllZoneUtil.getPlayerCardsInPlay(SourceCard.getController());
            LandInPlay = LandInPlay.getType("Land");
            String maxnumber = SpecialConditions.split("/")[1];
            if (!(LandInPlay.size() <= Integer.valueOf(maxnumber))) return false;
        }
        if (SpecialConditions.contains("LandOppCtrlLE")) {
            CardList OppLandInPlay = AllZoneUtil.getPlayerCardsInPlay(SourceCard.getController().getOpponent());
            OppLandInPlay = OppLandInPlay.getType("Land");
            String maxnumber = SpecialConditions.split("/")[1];
            if (!(OppLandInPlay.size() <= Integer.valueOf(maxnumber))) return false;
        }
        if (SpecialConditions.contains("OppCtrlMoreCreatures")) {
            CardList CreaturesInPlayYou = AllZoneUtil.getPlayerCardsInPlay(SourceCard.getController());
            CreaturesInPlayYou = CreaturesInPlayYou.getType("Creature");
            CardList CreaturesInPlayOpp = AllZoneUtil.getPlayerCardsInPlay(SourceCard.getController().getOpponent());
            CreaturesInPlayOpp = CreaturesInPlayOpp.getType("Creature");
            if (CreaturesInPlayYou.size() > CreaturesInPlayOpp.size()) return false;
        }
        if (SpecialConditions.contains("OppCtrlMoreLands")) {
            CardList LandsInPlayYou = AllZoneUtil.getPlayerCardsInPlay(SourceCard.getController());
            LandsInPlayYou = LandsInPlayYou.getType("Land");
            CardList LandsInPlayOpp = AllZoneUtil.getPlayerCardsInPlay(SourceCard.getController().getOpponent());
            LandsInPlayOpp = LandsInPlayOpp.getType("Land");
            if (LandsInPlayYou.size() > LandsInPlayOpp.size()) return false;
        }
        if (SpecialConditions.contains("EnchantedControllerCreaturesGE")) {
            CardList EnchantedControllerInPlay = AllZoneUtil.getPlayerCardsInPlay(SourceCard.getEnchantingCard().getController());
            EnchantedControllerInPlay = EnchantedControllerInPlay.getType("Creature");
            String maxnumber = SpecialConditions.split("/")[1];
            if (!(EnchantedControllerInPlay.size() >= Integer.valueOf(maxnumber))) return false;
        }
        if (SpecialConditions.contains("OppLifeLE")) {
            int life = SourceCard.getController().getOpponent().getLife();
            String maxnumber = SpecialConditions.split("/")[1];
            if (!(life <= Integer.valueOf(maxnumber))) return false;
        }
        if (SpecialConditions.contains("Threshold")) {
            if (!SourceCard.getController().hasThreshold()) return false;
        }
        if (SpecialConditions.contains("Imprint")) {
            if (SourceCard.getImprinted().isEmpty()) return false;
        }
        if (SpecialConditions.contains("Hellbent")) {
            CardList Handcards = AllZoneUtil.getPlayerHand(SourceCard.getController());
            if (Handcards.size() > 0) return false;
        }
        if (SpecialConditions.contains("Metalcraft")) {
            CardList CardsinPlay = AllZoneUtil.getPlayerCardsInPlay(SourceCard.getController());
            CardsinPlay = CardsinPlay.getType("Artifact");
            if (CardsinPlay.size() < 3) return false;
        }
        if (SpecialConditions.contains("isPresent")) { // is a card of a certain type/color present?
            String Requirements = SpecialConditions.replaceAll("isPresent ", "");
            CardList CardsinPlay = AllZoneUtil.getCardsInPlay();
            String Conditions[] = Requirements.split(",");
            CardsinPlay = CardsinPlay.getValidCards(Conditions, SourceCard.getController(), SourceCard);
            if (CardsinPlay.isEmpty()) return false;
        }
        if (SpecialConditions.contains("isInGraveyard")) { // is a card of a certain type/color present in yard?
            String Requirements = SpecialConditions.replaceAll("isInGraveyard ", "");
            CardList CardsinYards = AllZoneUtil.getCardsInGraveyard();
            String Conditions[] = Requirements.split(",");
            CardsinYards = CardsinYards.getValidCards(Conditions, SourceCard.getController(), SourceCard);
            if (CardsinYards.isEmpty()) return false;
        }
        if (SpecialConditions.contains("isNotPresent")) { // is no card of a certain type/color present?
            String Requirements = SpecialConditions.replaceAll("isNotPresent ", "");
            CardList CardsinPlay = AllZoneUtil.getCardsInPlay();
            String Conditions[] = Requirements.split(",");
            CardsinPlay = CardsinPlay.getValidCards(Conditions, SourceCard.getController(), SourceCard);
            if (!CardsinPlay.isEmpty()) return false;
        }
        if (SpecialConditions.contains("isEquipped")) {
            if (!SourceCard.isEquipped()) return false;
        }
        if (SpecialConditions.contains("isEnchanted")) {
            if (!SourceCard.isEnchanted()) return false;
        }
        if (SpecialConditions.contains("isUntapped")) {
            if (!SourceCard.isUntapped()) return false;
        }
        if (SpecialConditions.contains("isValid")) { // does this card meet the valid description?
            String Requirements = SpecialConditions.replaceAll("isValid ", "");
            if (!SourceCard.isValid(Requirements, SourceCard.getController(), SourceCard)) return false;
        }
        if (SpecialConditions.contains("isYourTurn")) {
            if (!AllZone.getPhase().isPlayerTurn(SourceCard.getController())) return false;
        }
        if (SpecialConditions.contains("notYourTurn")) {
            if (!AllZone.getPhase().isPlayerTurn(SourceCard.getController().getOpponent())) return false;
        }
        if (SpecialConditions.contains("OppPoisoned")) {
            if (SourceCard.getController().getOpponent().getPoisonCounters() == 0) return false;
        }
        if (SpecialConditions.contains("OppNotPoisoned")) {
            if (SourceCard.getController().getOpponent().getPoisonCounters() > 0) return false;
        }
        return true;

    }

    /** Constant <code>stLandManaAbilities</code> */
    public static Command stLandManaAbilities = new Command() {
        private static final long serialVersionUID = 8005448956536998277L;

        public void execute() {


            HashMap<String, String> produces = new HashMap<String, String>();
            /*
			 * for future use
			boolean naked = AllZoneUtil.isCardInPlay("Naked Singularity");
			boolean twist = AllZoneUtil.isCardInPlay("Reality Twist");
			//set up what they produce
			produces.put("Forest", naked || twist ? "B" : "G");
			produces.put("Island", naked == true ? "G" : "U");
			if(naked) produces.put("Mountain", "U");
			else if(twist) produces.put("Mountain", "W");
			else produces.put("Mountain", "R");
			produces.put("Plains", naked || twist ? "R" : "W");
			if(naked) produces.put("Swamp", "W");
			else if(twist) produces.put("Swamp", "G");
			else produces.put("Swamp", "B");
			*/
            produces.put("Forest", "G");
            produces.put("Island", "U");
            produces.put("Mountain", "R");
            produces.put("Plains", "W");
            produces.put("Swamp", "B");

            CardList lands = AllZoneUtil.getCardsInGame();
            lands = lands.filter(AllZoneUtil.lands);

            //remove all abilities granted by this Command
            for (Card land : lands) {
                ArrayList<Ability_Mana> sas = land.getManaAbility();
                for (SpellAbility sa : sas) {
                    if (sa.getType().equals("BasicLandTypeMana")) {
                        land.removeSpellAbility(sa);
                    }
                }
            }

            //add all appropriate mana abilities based on current types
            for (Card land : lands) {
                if (land.isType("Swamp")) {
                    AbilityFactory AF = new AbilityFactory();
                    SpellAbility sa = AF.getAbility("AB$ Mana | Cost$ T | Produced$ " + produces.get("Swamp") + " | SpellDescription$ Add " + produces.get("Swamp") + " to your mana pool.", land);
                    sa.setType("BasicLandTypeMana");
                    land.addSpellAbility(sa);
                }
                if (land.isType("Forest")) {
                    AbilityFactory AF = new AbilityFactory();
                    SpellAbility sa = AF.getAbility("AB$ Mana | Cost$ T | Produced$ " + produces.get("Forest") + " | SpellDescription$ Add " + produces.get("Forest") + " to your mana pool.", land);
                    sa.setType("BasicLandTypeMana");
                    land.addSpellAbility(sa);
                }
                if (land.isType("Island")) {
                    AbilityFactory AF = new AbilityFactory();
                    SpellAbility sa = AF.getAbility("AB$ Mana | Cost$ T | Produced$ " + produces.get("Island") + " | SpellDescription$ Add " + produces.get("Island") + " to your mana pool.", land);
                    sa.setType("BasicLandTypeMana");
                    land.addSpellAbility(sa);
                }
                if (land.isType("Mountain")) {
                    AbilityFactory AF = new AbilityFactory();
                    SpellAbility sa = AF.getAbility("AB$ Mana | Cost$ T | Produced$ " + produces.get("Mountain") + " | SpellDescription$ Add " + produces.get("Mountain") + " to your mana pool.", land);
                    sa.setType("BasicLandTypeMana");
                    land.addSpellAbility(sa);
                }
                if (land.isType("Plains")) {
                    AbilityFactory AF = new AbilityFactory();
                    SpellAbility sa = AF.getAbility("AB$ Mana | Cost$ T | Produced$ " + produces.get("Plains") + " | SpellDescription$ Add " + produces.get("Plains") + " to your mana pool.", land);
                    sa.setType("BasicLandTypeMana");
                    land.addSpellAbility(sa);
                }
            }
        }// execute()

    };//stLandManaAbilities
    
    /*
    /** Constant <code>stSetPT</code> 
    public static Command stSetPT = new Command() {
        /*
		 * Syntax: K:stSetPT:power:toughness:Description
		 * or (for Angry Mob/Gaea's Liege)
		 * K:stSetPT:power:toughness:condition:altPower:altToughness:Description
		 * or (for Levels)
		 * K:stSetPT:power:toughness:condition:altPower:altToughness:condition2:altPower2:altToughness2:Description
		 
        private static final long serialVersionUID = -8019071015309088017L;

        public void execute() {
            //gather cards in all zones based on rule 112.6a
            CardList Cards_WithKeyword = AllZoneUtil.getCardsInGame();
            Cards_WithKeyword = Cards_WithKeyword.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    if (c.getKeyword().toString().contains("stSetPT")) return true;
                    return false;
                }
            });

            // For each card found, find the keywords which are the stSetPT Keywords
            for (int i = 0; i < Cards_WithKeyword.size(); i++) {
                Card card = Cards_WithKeyword.get(i);
                ArrayList<String> a = card.getKeyword();
                for (int x = 0; x < a.size(); x++) {
                    if (a.get(x).toString().startsWith("stSetPT")) {
                        String parse = card.getKeyword().get(x).toString();
                        String k[] = parse.split(":");
                        /*for(int z = 0; z < k.length; z++) {
							System.out.println("k["+z+"]: "+k[z]);
						}
                        if (k.length < 2) {
                            System.out.println("Error in stSetPT for: " + card.getName());
                        } else {
                            //TODO - add some checking here...?
                            int power = 0;
                            int toughness = 0;
                            int altPower = 0;
                            int altToughness = 0;
                            boolean altCondition = false;

                            int altPower2 = 0;
                            int altToughness2 = 0;
                            boolean altCondition2 = false;

                            //double condition (for level creatures)
                            if (k.length > 6) {
                                String condition2 = k[6];
                                if (condition2.startsWith("LevelGE")) {
                                    condition2 = condition2.replace("LevelGE", "");
                                    int levelReq = Integer.parseInt(condition2);
                                    //System.out.println("condition2, got level: "+levelReq);
                                    if (card.getCounters(Counters.LEVEL) >= levelReq) {
                                        altCondition2 = true;
                                    }
                                }
                            }
                            //single condition (for Gaea's Liege/Angry Mob)
                            if (k.length > 3) {
                                String condition = k[3];
                                if (condition.equals("isAttacking")
                                        && card.isAttacking()) {
                                    altCondition = true;
                                } else if (condition.equals("isYourTurn")
                                        && AllZone.getPhase().isPlayerTurn(card.getController())) {
                                    altCondition = true;
                                } else if (condition.startsWith("LevelGE")) {
                                    condition = condition.replace("LevelGE", "");
                                    int levelReq = Integer.parseInt(condition);
                                    //System.out.println("condition, got level: "+levelReq);
                                    if (card.getCounters(Counters.LEVEL) >= levelReq) {
                                        altCondition = true;
                                    }
                                }
                            }

                            if (altCondition2) {
                                if (k.length > 6) {
                                    altPower2 = k[7].matches("[0-9][0-9]?") ? Integer.parseInt(k[7]) : CardFactoryUtil.xCount(card, k[7]);
                                }
                                if (k.length > 7) {
                                    altToughness2 = k[8].matches("[0-9][0-9]?") ? Integer.parseInt(k[8]) : CardFactoryUtil.xCount(card, k[8]);
                                }
                                card.setBaseAttack(altPower2);
                                card.setBaseDefense(altToughness2);
                            } else if (altCondition) {
                                //System.out.println("In alt condition");
                                //System.out.println("Setting power for ("+card.getName()+") to: "+altPower);
                                //System.out.println("Setting toughness for ("+card.getName()+") to: "+altToughness);
                                if (k.length > 4) {
                                    altPower = k[4].matches("[0-9][0-9]?") ? Integer.parseInt(k[4]) : CardFactoryUtil.xCount(card, k[4]);
                                }
                                if (k.length > 5) {
                                    altToughness = k[5].matches("[0-9][0-9]?") ? Integer.parseInt(k[5]) : CardFactoryUtil.xCount(card, k[5]);
                                }
                                card.setBaseAttack(altPower);
                                card.setBaseDefense(altToughness);
                            } else {
                                //use the base power/toughness to calculate
                                //System.out.println("Setting power for ("+card.getName()+") to: "+power);
                                //System.out.println("Setting toughness for ("+card.getName()+") to: "+toughness);
                                power = k[1].matches("[0-9][0-9]?") ? Integer.parseInt(k[1]) : CardFactoryUtil.xCount(card, k[1]);
                                toughness = k[2].matches("[0-9][0-9]?") ? Integer.parseInt(k[2]) : CardFactoryUtil.xCount(card, k[2]);
                                card.setBaseAttack(power);
                                card.setBaseDefense(toughness);
                            }
                        }

                    }
                }
            }
        }// execute()

    };//stSetPT */

    /** Constant <code>Coat_of_Arms</code> */
    public static Command Coat_of_Arms = new Command() {
        private static final long serialVersionUID = 583505612126735693L;

        CardList gloriousAnthemList = new CardList();

        public void execute() {
            CardList list = gloriousAnthemList;
            // reset all cards in list - aka "old" cards
            for (int i2 = 0; i2 < list.size(); i2++) {
                list.get(i2).addSemiPermanentAttackBoost(-1);
                list.get(i2).addSemiPermanentDefenseBoost(-1);
            }
            // add +1/+1 to cards
            list.clear();
            PlayerZone[] zone = getZone("Coat of Arms");

            // for each zone found add +1/+1 to each card
            for (int outer = 0; outer < zone.length; outer++) {
                CardList creature = AllZoneUtil.getCardsInPlay();

                for (int i = 0; i < creature.size(); i++) {
                    final Card crd = creature.get(i);
                    CardList Type = AllZoneUtil.getCardsInPlay();
                    Type = Type.filter(new CardListFilter() {
                        public boolean addCard(Card card) {
                            return !card.equals(crd) && card.isCreature() && !crd.getName().equals("Mana Pool");
                        }
                    });
                    CardList Already_Added = new CardList();
                    for (int x = 0; x < Type.size(); x++) {
                        Already_Added.clear();
                        for (int x2 = 0; x2 < Type.get(x).getType().size(); x2++) {
                            if (!Already_Added.contains(Type.get(x))) {
                                if (!Type.get(x).getType().get(x2).equals("Creature") && !Type.get(x).getType().get(x2).equals("Legendary")
                                        && !Type.get(x).getType().get(x2).equals("Artifact")) {
                                    if (crd.isType(Type.get(x).getType().get(x2))
                                            || crd.hasKeyword("Changeling")
                                            || Type.get(x).hasKeyword("Changeling")) {
                                        Already_Added.add(Type.get(x));
                                        crd.addSemiPermanentAttackBoost(1);
                                        crd.addSemiPermanentDefenseBoost(1);
                                        gloriousAnthemList.add(crd);
                                    }
                                }
                            }
                        }
                    }
                }// for inner
            }// for outer
        }// execute
    }; // Coat of Arms

    /**
     * stores the Command
     */
    public static Command Umbra_Stalker = new Command() {
        private static final long serialVersionUID = -3500747003228938898L;

        public void execute() {
            // get all creatures
            CardList cards = AllZoneUtil.getCardsInPlay("Umbra Stalker");
            for (Card c : cards) {
                Player player = c.getController();
                CardList grave = AllZoneUtil.getPlayerGraveyard(player);
                int pt = CardFactoryUtil.getNumberOfManaSymbolsByColor("B", grave);
                c.setBaseAttack(pt);
                c.setBaseDefense(pt);
            }
        }// execute()
    };

    /** Constant <code>Ajani_Avatar_Token</code> */
    public static Command Ajani_Avatar_Token = new Command() {
        private static final long serialVersionUID = 3027329837165436727L;

        public void execute() {
            CardList list = AllZoneUtil.getCardsInPlay();

            list = list.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return c.getName().equals("Avatar")
                            && c.getImageName().equals("W N N Avatar");
                }
            });
            for (int i = 0; i < list.size(); i++) {
                Card card = list.get(i);
                int n = card.getController().getLife();
                card.setBaseAttack(n);
                card.setBaseDefense(n);
            }// for
        }// execute
    }; // Ajani Avatar

    /** Constant <code>Old_Man_of_the_Sea</code> */
    public static Command Old_Man_of_the_Sea = new Command() {
        private static final long serialVersionUID = 8076177362922156784L;

        public void execute() {
            CardList list = AllZoneUtil.getCardsInPlay("Old Man of the Sea");
            for (Card oldman : list) {
                if (!oldman.getGainControlTargets().isEmpty()) {
                    if (oldman.getNetAttack() < oldman.getGainControlTargets().get(0).getNetAttack()) {
                        ArrayList<Command> coms = oldman.getGainControlReleaseCommands();
                        for (int i = 0; i < coms.size(); i++) {
                            coms.get(i).execute();
                        }
                    }
                }
            }
        }
    };//Old Man of the Sea

    /** Constant <code>Homarid</code> */
    public static Command Homarid = new Command() {
        private static final long serialVersionUID = 7156319758035295773L;

        public void execute() {
            CardList list = AllZoneUtil.getCardsInPlay("Homarid");

            for (Card homarid : list) {
                int tide = homarid.getCounters(Counters.TIDE);
                if (tide == 4) {
                    homarid.setCounter(Counters.TIDE, 0, true);
                }
            }
        }// execute()
    };

    /** Constant <code>Liu_Bei</code> */
    public static Command Liu_Bei = new Command() {

        private static final long serialVersionUID = 4235093010715735727L;

        public void execute() {
            CardList list = AllZoneUtil.getCardsInPlay("Liu Bei, Lord of Shu");

            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {

                    Card c = list.get(i);
                    if (getsBonus(c)) {
                        c.setBaseAttack(4);
                        c.setBaseDefense(6);
                    } else {
                        c.setBaseAttack(2);
                        c.setBaseDefense(4);
                    }

                }
            }
        }// execute()

        private boolean getsBonus(Card c) {
            CardList list = AllZoneUtil.getPlayerCardsInPlay(c.getController());
            list = list.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return c.getName().equals("Guan Yu, Sainted Warrior")
                            || c.getName().equals("Zhang Fei, Fierce Warrior");
                }
            });

            return list.size() > 0;
        }

    }; //Liu_Bei


    /** Constant <code>Sound_the_Call_Wolf</code> */
    public static Command Sound_the_Call_Wolf = new Command() {
        private static final long serialVersionUID = 4614281706799537283L;

        public void execute() {
            CardList list = AllZoneUtil.getCardsInPlay();
            list = list.filter(new CardListFilter() {
                public boolean addCard(Card c) {
                    return c.getName().equals("Wolf")
                            && c.hasKeyword("This creature gets +1/+1 for each card named Sound the Call in each graveyard.");
                }
            });

            for (int i = 0; i < list.size(); i++) {
                Card c = list.get(i);
                c.setBaseAttack(1 + countSoundTheCalls());
                c.setBaseDefense(c.getBaseAttack());
            }
        }

        private int countSoundTheCalls() {
            CardList list = AllZoneUtil.getCardsInGraveyard();
            list = list.getName("Sound the Call");
            return list.size();
        }

    }; //Sound_the_Call_Wolf

    /** Constant <code>Tarmogoyf</code> */
    public static Command Tarmogoyf = new Command() {
        private static final long serialVersionUID = 5895665460018262987L;

        public void execute() {
            // get all creatures
            CardList list = AllZoneUtil.getCardsInPlay("Tarmogoyf");

            for (int i = 0; i < list.size(); i++) {
                Card c = list.get(i);
                c.setBaseAttack(countDiffTypes());
                c.setBaseDefense(c.getBaseAttack() + 1);
            }

        }// execute()

        private int countDiffTypes() {
            CardList list = AllZoneUtil.getCardsInGraveyard();

            int count = 0;
            for (int q = 0; q < list.size(); q++) {
                if (list.get(q).isCreature()) {
                    count++;
                    break;
                }
            }
            for (int q = 0; q < list.size(); q++) {
                if (list.get(q).isSorcery()) {
                    count++;
                    break;
                }
            }
            for (int q = 0; q < list.size(); q++) {
                if (list.get(q).isInstant()) {
                    count++;
                    break;
                }
            }
            for (int q = 0; q < list.size(); q++) {
                if (list.get(q).isArtifact()) {
                    count++;
                    break;
                }
            }

            for (int q = 0; q < list.size(); q++) {
                if (list.get(q).isEnchantment()) {
                    count++;
                    break;
                }
            }

            for (int q = 0; q < list.size(); q++) {
                if (list.get(q).isLand()) {
                    count++;
                    break;
                }
            }

            for (int q = 0; q < list.size(); q++) {
                if (list.get(q).isPlaneswalker()) {
                    count++;
                    break;
                }
            }

            for (int q = 0; q < list.size(); q++) {
                if (list.get(q).isTribal()) {
                    count++;
                    break;
                }
            }
            return count;
        }
    };

    /** Constant <code>Muraganda_Petroglyphs</code> */
    public static Command Muraganda_Petroglyphs = new Command() {
        private static final long serialVersionUID = -6715848091817213517L;
        CardList gloriousAnthemList = new CardList();

        public void execute() {
            CardList list = gloriousAnthemList;
            Card c;
            // reset all cards in list - aka "old" cards
            for (int i = 0; i < list.size(); i++) {
                c = list.get(i);
                c.addSemiPermanentAttackBoost(-2);
                c.addSemiPermanentDefenseBoost(-2);
            }

            // add +2/+2 to vanilla cards
            list.clear();
            PlayerZone[] zone = getZone("Muraganda Petroglyphs");

            // for each zone found add +2/+2 to each vanilla card
            for (int outer = 0; outer < zone.length; outer++) {
                CardList creature = AllZoneUtil.getCreaturesInPlay();

                for (int i = 0; i < creature.size(); i++) {
                    c = creature.get(i);
                    if (((c.getAbilityText().trim().equals("") || c.isFaceDown()) && c.getUnhiddenKeyword().size() == 0)) {
                        c.addSemiPermanentAttackBoost(2);
                        c.addSemiPermanentDefenseBoost(2);

                        gloriousAnthemList.add(c);
                    }

                }// for inner
            }// for outer
        }// execute()
    }; // Muraganda_Petroglyphs

    /** Constant <code>Meddling_Mage</code> */
    public static Command Meddling_Mage = new Command() {
        private static final long serialVersionUID = 738264163993370439L;
        CardList gloriousAnthemList = new CardList();

        public void execute() {
            CardList list = gloriousAnthemList;
            Card c;
            // reset all cards in list - aka "old" cards
            for (int i = 0; i < list.size(); i++) {
                c = list.get(i);
                //c.removeIntrinsicKeyword("This card can't be cast");
                c.setUnCastable(false);
            }

            list.clear();

            CardList cl = AllZoneUtil.getCardsInPlay("Meddling Mage");

            for (int i = 0; i < cl.size(); i++) {
                final Card crd = cl.get(i);

                CardList spells = new CardList();
                spells.addAll(AllZoneUtil.getPlayerGraveyard(AllZone.getHumanPlayer()));
                spells.addAll(AllZoneUtil.getPlayerHand(AllZone.getHumanPlayer()));
                spells.addAll(AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer()));
                spells.addAll(AllZoneUtil.getPlayerGraveyard(AllZone.getComputerPlayer()));
                spells = spells.filter(new CardListFilter() {
                    public boolean addCard(Card c) {
                        return !c.isLand()
                                && c.getName().equals(
                                crd.getNamedCard());
                    }
                });

                for (int j = 0; j < spells.size(); j++) {
                    c = spells.get(j);
                    if (!c.isLand()) {
                        //c.addIntrinsicKeyword("This card can't be cast");
                        c.setUnCastable(true);
                        gloriousAnthemList.add(c);
                    }
                }// for inner
            }// for outer
        }// execute()
    }; // Meddling_Mage

    /** Constant <code>Gaddock_Teeg</code> */
    public static Command Gaddock_Teeg = new Command() {
        private static final long serialVersionUID = -479252814191086571L;
        CardList gloriousAnthemList = new CardList();

        public void execute() {
            CardList list = gloriousAnthemList;
            Card c;
            // reset all cards in list - aka "old" cards
            for (int i = 0; i < list.size(); i++) {
                c = list.get(i);
                //c.removeIntrinsicKeyword("This card can't be cast");
                c.setUnCastable(false);
            }

            list.clear();

            CardList cl = AllZoneUtil.getCardsInPlay("Gaddock Teeg");

            for (int i = 0; i < cl.size(); i++) {
                CardList spells = new CardList();
                spells.addAll(AllZoneUtil.getPlayerGraveyard(AllZone.getHumanPlayer()));
                spells.addAll(AllZoneUtil.getPlayerHand(AllZone.getHumanPlayer()));
                spells.addAll(AllZoneUtil.getPlayerHand(AllZone.getComputerPlayer()));
                spells.addAll(AllZoneUtil.getPlayerGraveyard(AllZone.getComputerPlayer()));

                spells = spells.filter(new CardListFilter() {
                    public boolean addCard(Card c) {

                        boolean isXNonCreature = false;
                        if (c.getSpellAbility().length > 0) {
                            if (c.getSpellAbility()[0].isXCost())
                                isXNonCreature = true;
                        }

                        return !c.isLand()
                                && !c.isCreature()
                                && (CardUtil.getConvertedManaCost(c.getManaCost()) >= 4 || isXNonCreature);
                    }
                });

                for (int j = 0; j < spells.size(); j++) {
                    c = spells.get(j);
                    if (!c.isLand()) {
                        c.setUnCastable(true);
                        gloriousAnthemList.add(c);
                    }
                }// for inner
            }// for outer
        }// execute()
    }; //

    /** Constant <code>Iona_Shield_of_Emeria</code> */
    public static Command Iona_Shield_of_Emeria = new Command() {
        private static final long serialVersionUID = 7349652597673216545L;
        CardList gloriousAnthemList = new CardList();

        public void execute() {
            CardList list = gloriousAnthemList;
            Card c;
            // reset all cards in list - aka "old" cards
            for (int i = 0; i < list.size(); i++) {
                c = list.get(i);
                //c.removeIntrinsicKeyword("This card can't be cast");
                c.setUnCastable(false);
            }

            list.clear();

            CardList cl = AllZoneUtil.getCardsInPlay("Iona, Shield of Emeria");

            for (int i = 0; i < cl.size(); i++) {
                final Card crd = cl.get(i);
                Player controller = cl.get(i).getController();
                Player opp = controller.getOpponent();

                CardList spells = new CardList();
                spells.addAll(AllZoneUtil.getPlayerGraveyard(opp));
                spells.addAll(AllZoneUtil.getPlayerHand(opp));

                spells = spells.filter(new CardListFilter() {
                    public boolean addCard(Card c) {
                        return !c.isLand()
                                && CardUtil.getColors(c).contains(
                                crd.getChosenColor());
                    }
                });

                for (int j = 0; j < spells.size(); j++) {
                    c = spells.get(j);
                    if (!c.isLand()) {
                        c.setUnCastable(true);
                        gloriousAnthemList.add(c);
                    }
                }// for inner
            }// for outer
        }// execute()
    }; //end Iona, Shield of Emeria

    // returns all PlayerZones that has at least 1 Glorious Anthem
    // if Computer has 2 Glorious Anthems, AllZone.getComputerPlay() will be
    // returned twice
    /**
     * <p>getZone.</p>
     *
     * @param cardName a {@link java.lang.String} object.
     * @return an array of {@link forge.PlayerZone} objects.
     */
    private static PlayerZone[] getZone(String cardName) {
        CardList all = AllZoneUtil.getCardsInPlay();

        ArrayList<PlayerZone> zone = new ArrayList<PlayerZone>();
        for (int i = 0; i < all.size(); i++) {
            Card c = all.get(i);
            if (c.getName().equals(cardName) && !c.isFaceDown()) zone.add(AllZone.getZone(c));
        }

        PlayerZone[] z = new PlayerZone[zone.size()];
        zone.toArray(z);
        return z;
    }

    /** Constant <code>commands</code> */
    public static HashMap<String, Command> commands = new HashMap<String, Command>();

    static {
        //Please add cards in alphabetical order so they are easier to find

        commands.put("Ajani_Avatar_Token", Ajani_Avatar_Token);
        commands.put("Coat_of_Arms", Coat_of_Arms);
        commands.put("Conspiracy", Conspiracy);
        commands.put("Elspeth_Emblem", Elspeth_Emblem);
        commands.put("Favor_of_the_Mighty", Favor_of_the_Mighty);
        commands.put("Gaddock_Teeg", Gaddock_Teeg);
        commands.put("Homarid", Homarid);
        commands.put("Iona_Shield_of_Emeria", Iona_Shield_of_Emeria);

        commands.put("Koth_Emblem", Koth_Emblem);
        commands.put("Liu_Bei", Liu_Bei);

        commands.put("Meddling_Mage", Meddling_Mage);
        commands.put("Muraganda_Petroglyphs", Muraganda_Petroglyphs);

        commands.put("Old_Man_of_the_Sea", Old_Man_of_the_Sea);

        commands.put("Sound_the_Call_Wolf", Sound_the_Call_Wolf);
        commands.put("Tarmogoyf", Tarmogoyf);

        commands.put("Umbra_Stalker", Umbra_Stalker);

        ///The commands above are in alphabetical order by cardname.
    }

    /** Constant <code>stAnimate</code> */
    public static Command stAnimate = new Command() {
        /** stAnimate
         * Syntax:[ k[0] stAnimate[All][Self][Enchanted] 		: k[1] AnimateValid : 
         * 			k[2] P/T/Keyword : k[3] extra types 		: k[4] extra colors : 
         * 			k[5] Abilities	 : k[6] Special Conditions 	: k[7] Description
         *
         */

        private static final long serialVersionUID = -1404133561787349004L;

        // storage stores the source card and the cards it gave its bonus to, to know what to remove
        private ArrayList<StaticEffect> storage = new ArrayList<StaticEffect>();

        public void execute() {

            // remove all static effects
            for (int i = 0; i < storage.size(); i++) {
                removeStaticEffect(storage.get(i));
            }

            //clear the list
            storage = new ArrayList<StaticEffect>();

            //Gather Cards on the Battlefield with the stPump Keyword
            CardList cards = AllZoneUtil.getCardsInPlay();
            cards.getKeywordsContain("stAnimate");

            // check each card
            for (int i = 0; i < cards.size(); i++) {
                Card cardWithKeyword = cards.get(i);
                ArrayList<String> keywords = cardWithKeyword.getKeyword();

                // check each keyword of the card
                for (int j = 0; j < keywords.size(); j++) {
                    String keyword = keywords.get(j);

                    if (keyword.startsWith("stAnimate")) {
                        StaticEffect se = new StaticEffect();     //create a new StaticEffect
                        se.setSource(cardWithKeyword);
                        se.setKeywordNumber(j);


                        //get the affected cards
                        String k[] = keyword.split(":", 8);

                        if (specialConditionsMet(cardWithKeyword, k[6])) { //special conditions are isPresent, isValid

                            final String affected = k[1];
                            final String specific[] = affected.split(",");
                            CardList affectedCards = getAffectedCards(cardWithKeyword, k, specific); // options are All, Self, Enchanted etc.
                            se.setAffectedCards(affectedCards);

                            String[] pt = k[2].split("/");
                            if (!k[2].equals("no changes") && pt.length > 1) {

                                int x = 0;
                                if (pt[0].contains("X") || pt[1].contains("X"))
                                    x = CardFactoryUtil.xCount(cardWithKeyword, cardWithKeyword.getSVar("X").split("\\$")[1]);
                                se.setXValue(x);

                                int y = 0;
                                if (pt[1].contains("Y"))
                                    y = CardFactoryUtil.xCount(cardWithKeyword, cardWithKeyword.getSVar("Y").split("\\$")[1]);
                                se.setYValue(y);
                            }

                            ArrayList<String> types = new ArrayList<String>();
                            if (!k[3].equalsIgnoreCase("no types")) {
                                types.addAll(Arrays.asList(k[3].split(",")));
                                if (types.contains("Overwrite")) {
                                    types.remove("Overwrite");
                                    se.setOverwriteTypes(true);
                                }
                                if (types.contains("KeepSupertype")) {
                                    types.remove("KeepSupertype");
                                    se.setKeepSupertype(true);
                                }
                                if (types.contains("RemoveSubTypes")) {
                                    types.remove("RemoveSubTypes");
                                    se.setRemoveSubTypes(true);
                                }
                            }

                            String colors = "";
                            if (!k[4].equalsIgnoreCase("no colors")) {
                                colors = k[4];
                                if (colors.contains(",Overwrite") || colors.contains("Overwrite")) {
                                    colors = colors.replace(",Overwrite", "");
                                    colors = colors.replace("Overwrite", "");
                                    se.setOverwriteColors(true);
                                }
                                colors = CardUtil.getShortColorsString(new ArrayList<String>(Arrays.asList(k[4].split(","))));
                            }

                            if (k[2].contains("Overwrite")) {
                                se.setOverwriteKeywords(true);
                            }

                            if (k[5].contains("Overwrite")) {
                                se.setOverwriteAbilities(true);
                            }

                            addStaticEffects(se, cardWithKeyword, affectedCards, k[2], types, colors); //give the boni to the affected cards

                            storage.add(se); // store the information
                        }
                    }
                }
            }
        }// execute()

        private void addStaticEffects(StaticEffect se, Card source, CardList affectedCards, String details, ArrayList<String> types, String colors) {

            for (int i = 0; i < affectedCards.size(); i++) {
                Card affectedCard = affectedCards.get(i);

                if (!details.equals("no changes")) {
                    String[] keyword = details.split("/", 3);
                    String powerStr = keyword[0];
                    String toughStr = keyword[1];
                    //copied from stSetPT power/toughness
                    if (!powerStr.equals("no change")) {
                        int power = powerStr.matches("[0-9][0-9]?") ? Integer.parseInt(powerStr) : CardFactoryUtil.xCount(affectedCard, powerStr);
                        se.addOriginalPT(affectedCard, affectedCard.getBaseAttack(), affectedCard.getBaseDefense());
                        affectedCard.setBaseAttack(power);
                    }
                    if (!toughStr.equals("no change")) {
                        int toughness = toughStr.matches("[0-9][0-9]?") ? Integer.parseInt(toughStr) : CardFactoryUtil.xCount(affectedCard, toughStr);
                        se.addOriginalPT(affectedCard, affectedCard.getBaseAttack(), affectedCard.getBaseDefense());
                        affectedCard.setBaseDefense(toughness);
                    }
                    if (se.isOverwriteKeywords()) {
                        se.addOriginalKeywords(affectedCard, affectedCard.getIntrinsicKeyword());
                        affectedCard.clearAllKeywords();
                    } else {
                        if (keyword.length != 2) {
                            int index = 2;
                            if (keyword.length == 1) index = 0;
                            String keywords[] = keyword[index].split(" & ");
                            for (int j = 0; j < keywords.length; j++) {
                                String kw = keywords[j];
                                affectedCard.addExtrinsicKeyword(kw);
                            }
                        }
                    }
                }

                if (se.isOverwriteTypes()) {
                    se.addOriginalTypes(affectedCard, affectedCard.getType());
                    if (!se.isKeepSupertype()) {
                        affectedCard.clearAllTypes();
                    } else {
                        ArrayList<String> acTypes = affectedCard.getType();
                        for (String t : acTypes) {
                            if (!CardUtil.isASuperType(t)) affectedCard.removeType(t);
                        }
                    }
                }
                if (se.isRemoveSubTypes()) {
                    se.addOriginalTypes(affectedCard, affectedCard.getType());
                    ArrayList<String> acTypes = affectedCard.getType();
                    for (String t : acTypes) {
                        if (CardUtil.isASubType(t)) affectedCard.removeType(t);
                    }
                }
                for (String type : types) {
                    if (!affectedCard.isType(type)) {
                        affectedCard.addType(type);
                        se.addType(affectedCard, type);
                    } else {
                        se.removeType(affectedCard, type);
                    }
                }
                //Abilities
                if (se.isOverwriteAbilities()) {
                    se.addOriginalAbilities(affectedCard, affectedCard.getAllButFirstSpellAbility());
                    affectedCard.clearAllButFirstSpellAbility();
                } else {
                    //TODO - adding SpellAbilities statically here not supported at this time
                }

                long t = affectedCard.addColor(colors, affectedCard, !se.isOverwriteColors(), true);
                se.addTimestamp(affectedCard, t);
            }//end for
        }

        void removeStaticEffect(StaticEffect se) {
            Card source = se.getSource();
            CardList affected = se.getAffectedCards();
            int num = se.getKeywordNumber();
            String parse = source.getKeyword().get(num).toString();
            String k[] = parse.split(":");
            for (int i = 0; i < affected.size(); i++) {
                Card c = affected.get(i);
                removeStaticEffect(se, source, c, k);
            }
            se.clearAllTypes();
            se.clearTimestamps();
        }

        private void removeStaticEffect(StaticEffect se, Card source, Card affectedCard, String[] details) {

            String[] kw = details[2].split("/", 3);

            if (!details[2].equals("no changes")) {
                if (!kw[0].equals("no change"))
                    affectedCard.setBaseAttack(se.getOriginalPower(affectedCard));
                if (!kw[1].equals("no change"))
                    affectedCard.setBaseDefense(se.getOriginalToughness(affectedCard));
            }

            for (String type : se.getTypes(affectedCard)) {
                affectedCard.removeType(type);
            }
            if (se.isOverwriteTypes()) {
                for (String type : se.getOriginalTypes(affectedCard)) {
                    if (!se.isKeepSupertype() || (se.isKeepSupertype() && !CardUtil.isASuperType(type))) {
                        affectedCard.addType(type);
                    }
                }
            }
            if (se.isRemoveSubTypes()) {
                for (String type : se.getOriginalTypes(affectedCard)) {
                    if (CardUtil.isASubType(type)) affectedCard.addType(type);
                }
            }

            if (se.isOverwriteKeywords()) {
                for (String keyw : se.getOriginalKeywords(affectedCard)) affectedCard.addIntrinsicKeyword(keyw);
            } else {
                if (kw.length != 2) {
                    int index = 2;
                    if (kw.length == 1) index = 0;
                    String keywords[] = kw[index].split(" & ");
                    for (int j = 0; j < keywords.length; j++) {
                        String keyw = keywords[j];
                        affectedCard.removeExtrinsicKeyword(keyw);
                    }
                }
            }
            //Abilities
            if (se.isOverwriteAbilities()) {
                for (SpellAbility sa : se.getOriginalAbilities(affectedCard)) affectedCard.addSpellAbility(sa);
            } else {
                //TODO - adding SpellAbilities statically here not supported at this time
            }

            affectedCard.removeColor(se.getColorDesc(), affectedCard, !se.isOverwriteColors(), se.getTimestamp(affectedCard));
        }//end removeStaticEffects

        private CardList getAffectedCards(Card source, String[] details, String[] specific) {
            // [Self], [All], [Enchanted]
            CardList affected = new CardList();
            String range = details[0].replaceFirst("stAnimate", "");

            if (range.equals("Self")) {
                affected.add(source);
            } else if (range.equals("All")) {
                affected.addAll(AllZoneUtil.getCardsInPlay());
            } else if (range.equals("Enchanted")) {
                if (source.getEnchanting().size() > 0) {
                    affected.addAll(source.getEnchanting().toArray());
                }
            }
            affected = affected.getValidCards(specific, source.getController(), source);

            return affected;
        }//end getAffectedCards()
    };


    /**
     * <p>doPowerSink.</p>
     *
     * @param p a {@link forge.Player} object.
     */
    public static void doPowerSink(Player p) {
        //get all lands with mana abilities
        CardList lands = AllZoneUtil.getPlayerLandsInPlay(p);
        lands = lands.filter(new CardListFilter() {
            public boolean addCard(Card c) {
                return c.getManaAbility().size() > 0;
            }
        });
        //tap them
        for (Card c : lands) c.tap();

        //empty mana pool
        if (p.isHuman()) AllZone.getManaPool().clearPool();
        else AllZone.getComputerManaPool().clearPool();
    }

}//end class GameActionUtil
