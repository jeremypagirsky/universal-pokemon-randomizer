package com.dabomstew.pkrandom;

import com.dabomstew.pkrandom.pokemon.GenRestrictions;
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.romhandlers.Gen1RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen2RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen3RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen4RomHandler;
import com.dabomstew.pkrandom.romhandlers.Gen5RomHandler;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;

// TODO(kjs): split each section into own class?
public class Settings {

  public static final int VERSION = 163;

  public static final Map<String, RomHandler.Factory> ROM_HANDLER_FACTORIES;
  static {
    Map<String, RomHandler.Factory> map = new HashMap<>();

    RomHandler.Factory gen1 = new Gen1RomHandler.Factory();
    RomHandler.Factory gen2 = new Gen2RomHandler.Factory();
    RomHandler.Factory gen3 = new Gen3RomHandler.Factory();
    RomHandler.Factory gen4 = new Gen4RomHandler.Factory();
    RomHandler.Factory gen5 = new Gen5RomHandler.Factory();
    // NB: We don't need to use RandomSource because calling
    // getROMName() is not randomized.
    Random r = new Random();

    map.put(gen1.create(r).getROMName(), gen1);
    map.put(gen2.create(r).getROMName(), gen2);
    map.put(gen3.create(r).getROMName(), gen3);
    map.put(gen4.create(r).getROMName(), gen4);
    map.put(gen5.create(r).getROMName(), gen5);

    ROM_HANDLER_FACTORIES = Collections.unmodifiableMap(map);
  }

  private byte[] trainerClasses;
  private byte[] trainerNames;
  private byte[] nicknames;

  private RomHandler.Factory romHandlerFactory;
  private GenRestrictions currentRestrictions;
  private int currentCodeTweaks;

  private boolean updateTypeEffectiveness;
  private boolean updateMoves;
  private boolean updateMovesLegacy;
  private boolean changeImpossibleEvolutions;
  private boolean makeEvolutionsEasier;
  private boolean lowerCasePokemonNames;
  private boolean nationalDexAtStart;
  private boolean raceMode;
  private boolean randomizeHiddenHollows;
  private boolean useCodeTweaks;
  private boolean allowBrokenMoves = true;
  private boolean limitPokemon;

  public enum BaseStatisticsMod {
    UNCHANGED,
    SHUFFLE,
    RANDOM_FOLLOW_EVOLUTIONS,
    COMPLETELY_RANDOM,
  }
  private BaseStatisticsMod baseStatisticsMod = BaseStatisticsMod.UNCHANGED;
  private boolean standardizeEXPCurves;

  public enum AbilitiesMod {
    UNCHANGED,
    RANDOMIZE
  }
  private AbilitiesMod abilitiesMod = AbilitiesMod.UNCHANGED;
  private boolean allowWonderGuard = true;

  public enum StartersMod {
    UNCHANGED,
    CUSTOM,
    COMPLETELY_RANDOM,
    RANDOM_WITH_TWO_EVOLUTIONS
  }
  private StartersMod startersMod = StartersMod.UNCHANGED;
  private Pokemon[] customStarters = new Pokemon[3];
  private boolean randomizeStartersHeldItems;

  public enum TypesMod {
    UNCHANGED,
    RANDOM_FOLLOW_EVOLUTIONS,
    COMPLETELY_RANDOM
  }
  private TypesMod typesMod = TypesMod.UNCHANGED;

  public enum MovesetsMod {
    UNCHANGED,
    RANDOM_PREFER_SAME_TYPE,
    COMPLETELY_RANDOM,
    METRONOME_ONLY
  }
  private MovesetsMod movesetsMod = MovesetsMod.UNCHANGED;
  private boolean startWithFourMoves;

  public enum TrainersMod {
    UNCHANGED,
    RANDOM,
    TYPE_THEMED
  }
  private TrainersMod trainersMod = TrainersMod.UNCHANGED;
  private boolean rivalCarriesStarterThroughout;
  private boolean trainersUsePokemonOfSimilarStrength;
  private boolean trainersMatchTypingDistribution;
  private boolean trainersCanUseLegendaries = true;
  private boolean trainersEarlyWonderGuard = true;
  private boolean randomizeTrainerNames;
  private boolean randomizeTrainerClassNames;

  public enum WildPokemonMod {
    UNCHANGED,
    RANDOM,
    AREA_MAPPING,
    GLOBAL_MAPPING
  }
  public enum WildPokemonRestrictionMod {
    NONE,
    SIMILAR_STRENGTH,
    CATCH_EM_ALL,
    TYPE_THEME_AREAS
  }
  private WildPokemonMod wildPokemonMod = WildPokemonMod.UNCHANGED;
  private WildPokemonRestrictionMod wildPokemonRestrictionMod = WildPokemonRestrictionMod.NONE;
  private boolean useTimeBasedEncounters;
  private boolean encounterLegendaries = true;
  private boolean useMinimumCatchRate;
  private boolean randomizeWildPokemonHeldItems;

  public enum StaticPokemonMod {
    UNCHANGED,
    RANDOM_MATCHING,
    COMPLETELY_RANDOM
  }
  private StaticPokemonMod staticPokemonMod = StaticPokemonMod.UNCHANGED;

  public enum TMsMod {
    UNCHANGED,
    RANDOM
  }
  private TMsMod tmsMod = TMsMod.UNCHANGED;
  private boolean tmLevelUpMoveSanity;
  private boolean keepFieldMoves;

  public enum TMsHMsCompatibilityMod {
    UNCHANGED,
    RANDOM_PREFER_TYPE,
    COMPLETELY_RANDOM,
    FULL
  }
  private TMsHMsCompatibilityMod tmsHmsCompatibilityMod = TMsHMsCompatibilityMod.UNCHANGED;

  public enum MoveTutorMovesMod {
    UNCHANGED,
    RANDOM
  }
  private MoveTutorMovesMod moveTutorMovesMod = MoveTutorMovesMod.UNCHANGED;
  private boolean tutorLevelUpMoveSanity;
  private boolean keepFieldMoveTutors;

  public enum MoveTutorsCompatibilityMod {
    UNCHANGED,
    RANDOM_PREFER_TYPE,
    COMPLETELY_RANDOM,
    FULL
  }
  private MoveTutorsCompatibilityMod moveTutorsCompatibilityMod = MoveTutorsCompatibilityMod.UNCHANGED;

  public enum InGameTradesMod {
    UNCHANGED,
    RANDOMIZE_GIVEN,
    RANDOMIZE_GIVEN_AND_REQUESTED
  }
  private InGameTradesMod inGameTradesMod = InGameTradesMod.UNCHANGED;
  private boolean randomizeInGameTradesNicknames;
  private boolean randomizeInGameTradesOTs;
  private boolean randomizeInGameTradesIVs;
  private boolean randomizeInGameTradesItems;

  public enum FieldItemsMod {
    UNCHANGED,
    SHUFFLE,
    RANDOM
  }
  private FieldItemsMod fieldItemsMod = FieldItemsMod.UNCHANGED;

  public void write(FileOutputStream out) throws IOException {
    out.write(VERSION);
    byte[] settings = toString().getBytes("UTF-8");
    out.write(settings.length);
    out.write(settings);
  }

  public static Settings read(FileInputStream in)
      throws IOException, UnsupportedOperationException {
    int version = in.read();
    if (version > VERSION) {
      throw new UnsupportedOperationException(
          "Cannot read settings from a newer version of the randomizer.");
    }
    int length = in.read();
    byte[] buffer = new byte[length];
    in.read(buffer);
    String settings = new String(buffer, "UTF-8");
    if (version < VERSION) {
      // TODO(kjs): display warning somehow...
      settings = new LegacyParser().update(version, settings);
    }
    return fromString(settings);
  }

  @Override
  public String toString() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    // 0: general options #1 + trainer/class names
    out.write(makeByteSelected(
        lowerCasePokemonNames,
        nationalDexAtStart,
        changeImpossibleEvolutions,
        updateMoves,
        updateMovesLegacy,
        updateTypeEffectiveness,
        randomizeTrainerNames,
        randomizeTrainerClassNames));

    // 1: pokemon base stats & abilities
    out.write(makeByteSelected(
        baseStatisticsMod == BaseStatisticsMod.RANDOM_FOLLOW_EVOLUTIONS,
        baseStatisticsMod == BaseStatisticsMod.COMPLETELY_RANDOM,
        baseStatisticsMod == BaseStatisticsMod.SHUFFLE,
        baseStatisticsMod == BaseStatisticsMod.UNCHANGED,
        abilitiesMod == AbilitiesMod.UNCHANGED,
        abilitiesMod == AbilitiesMod.RANDOMIZE,
        !allowWonderGuard,
        standardizeEXPCurves));

    // 2: pokemon types & more general options
    out.write(makeByteSelected(
        typesMod == TypesMod.RANDOM_FOLLOW_EVOLUTIONS,
        typesMod == TypesMod.COMPLETELY_RANDOM,
        typesMod == TypesMod.UNCHANGED,
        useCodeTweaks,
        raceMode,
        randomizeHiddenHollows,
        !allowBrokenMoves,
        limitPokemon));

    // v162: 3: new general options byte added (the rest were full)

    out.write(makeByteSelected(makeEvolutionsEasier));

    // 4: starter pokemon stuff
    out.write(makeByteSelected(
        startersMod == StartersMod.CUSTOM,
        startersMod == StartersMod.COMPLETELY_RANDOM,
        startersMod == StartersMod.UNCHANGED,
        startersMod == StartersMod.RANDOM_WITH_TWO_EVOLUTIONS,
        randomizeStartersHeldItems));

    // @5 dropdowns
    writePokemonIndex(out, customStarters[0]);
    writePokemonIndex(out, customStarters[1]);
    writePokemonIndex(out, customStarters[2]);

    // 11 movesets
    out.write(makeByteSelected(
        movesetsMod == MovesetsMod.COMPLETELY_RANDOM,
        movesetsMod == MovesetsMod.RANDOM_PREFER_SAME_TYPE,
        movesetsMod == MovesetsMod.UNCHANGED,
        movesetsMod == MovesetsMod.METRONOME_ONLY,
        startWithFourMoves));

    // 12 trainer pokemon
    // changed 160
    out.write(makeByteSelected(
        trainersUsePokemonOfSimilarStrength,
        trainersMod == TrainersMod.RANDOM,
        rivalCarriesStarterThroughout,
        trainersMod == TrainersMod.TYPE_THEMED,
        trainersMatchTypingDistribution,
        trainersMod == TrainersMod.UNCHANGED,
        !trainersCanUseLegendaries,
        !trainersEarlyWonderGuard));

    // 13 wild pokemon
    out.write(makeByteSelected(
        wildPokemonRestrictionMod == WildPokemonRestrictionMod.CATCH_EM_ALL,
        wildPokemonMod == WildPokemonMod.AREA_MAPPING,
        wildPokemonRestrictionMod == WildPokemonRestrictionMod.NONE,
        wildPokemonRestrictionMod == WildPokemonRestrictionMod.TYPE_THEME_AREAS,
        wildPokemonMod == WildPokemonMod.GLOBAL_MAPPING,
        wildPokemonMod == WildPokemonMod.RANDOM,
        wildPokemonMod == WildPokemonMod.UNCHANGED,
        useTimeBasedEncounters));

    // 14 wild pokemon 2
    // bugfix 161
    out.write(makeByteSelected(
        useMinimumCatchRate,
        !encounterLegendaries,
        wildPokemonRestrictionMod == WildPokemonRestrictionMod.SIMILAR_STRENGTH,
        randomizeWildPokemonHeldItems));

    // 15 static pokemon
    out.write(makeByteSelected(
        staticPokemonMod == StaticPokemonMod.UNCHANGED,
        staticPokemonMod == StaticPokemonMod.RANDOM_MATCHING,
        staticPokemonMod == StaticPokemonMod.COMPLETELY_RANDOM));

    // 16 tm randomization
    // new stuff 162
    out.write(makeByteSelected(
        tmsHmsCompatibilityMod == TMsHMsCompatibilityMod.COMPLETELY_RANDOM,
        tmsHmsCompatibilityMod == TMsHMsCompatibilityMod.RANDOM_PREFER_TYPE,
        tmsHmsCompatibilityMod == TMsHMsCompatibilityMod.UNCHANGED,
        tmsMod == TMsMod.RANDOM,
        tmsMod == TMsMod.UNCHANGED,
        tmLevelUpMoveSanity,
        keepFieldMoves,
        tmsHmsCompatibilityMod == TMsHMsCompatibilityMod.FULL));

    // 17 move tutor randomization
    out.write(makeByteSelected(
        moveTutorsCompatibilityMod == MoveTutorsCompatibilityMod.COMPLETELY_RANDOM,
        moveTutorsCompatibilityMod == MoveTutorsCompatibilityMod.RANDOM_PREFER_TYPE,
        moveTutorsCompatibilityMod == MoveTutorsCompatibilityMod.UNCHANGED,
        moveTutorMovesMod == MoveTutorMovesMod.RANDOM,
        moveTutorMovesMod == MoveTutorMovesMod.UNCHANGED,
        tutorLevelUpMoveSanity,
        keepFieldMoveTutors,
        moveTutorsCompatibilityMod == MoveTutorsCompatibilityMod.FULL));

    // new 150
    // 18 in game trades
    out.write(makeByteSelected(
        inGameTradesMod == InGameTradesMod.RANDOMIZE_GIVEN_AND_REQUESTED,
        inGameTradesMod == InGameTradesMod.RANDOMIZE_GIVEN,
        randomizeInGameTradesItems,
        randomizeInGameTradesIVs,
        randomizeInGameTradesNicknames,
        randomizeInGameTradesOTs,
        inGameTradesMod == InGameTradesMod.UNCHANGED));

    // 19 field items
    out.write(makeByteSelected(
        fieldItemsMod == FieldItemsMod.RANDOM,
        fieldItemsMod == FieldItemsMod.SHUFFLE,
        fieldItemsMod == FieldItemsMod.UNCHANGED));

    // @ 20 pokemon restrictions
    try {
      if (currentRestrictions != null) {
        writeFullInt(out, currentRestrictions.toInt());
      } else {
        writeFullInt(out, 0);
      }
    } catch (IOException e) {
    }

    // @ 24 code tweaks
    try {
      writeFullInt(out, currentCodeTweaks);
    } catch (IOException e) {

    }

    try {
      byte[] romName = getRomHandler().getROMName().getBytes("US-ASCII");
      out.write(romName.length);
      out.write(romName);
    } catch (UnsupportedEncodingException e) {
      out.write(0);
    } catch (IOException e) {
      out.write(0);
    }

    byte[] current = out.toByteArray();
    CRC32 checksum = new CRC32();
    checksum.update(current);

    try {
      writeFullInt(out, (int) checksum.getValue());
      writeFullInt(out, FileFunctions.getFileChecksum("trainerclasses.txt"));
      writeFullInt(out, FileFunctions.getFileChecksum("trainernames.txt"));
      writeFullInt(out, FileFunctions.getFileChecksum("nicknames.txt"));
    } catch (IOException e) {
    }

    return DatatypeConverter.printBase64Binary(out.toByteArray());
  }

  public static Settings fromString(String str) throws UnsupportedEncodingException {
    byte[] data = DatatypeConverter.parseBase64Binary(str);
    checkChecksum(data);

    // Read the ROM handler name in order to restore the handler
    int romNameLength = FileFunctions.readFullInt(data, 25);
    // int romNameLength = data[28] & 0xFF;
    String romName = new String(data, 29, romNameLength, "US-ASCII");

    return fromStringWithRomHandlerFactory(str, ROM_HANDLER_FACTORIES.get(romName));
  }

  @Deprecated
  public static Settings fromStringWithRomHandlerFactory(
      String str, RomHandler.Factory romHandlerFactory) {

    byte[] data = DatatypeConverter.parseBase64Binary(str);

    checkChecksum(data);

    Settings settings = new Settings();
    settings.setRomHandlerFactory(romHandlerFactory);

    // Restore the actual controls
    settings.setLowerCasePokemonNames(restoreState(data[0], 0));
    settings.setNationalDexAtStart(restoreState(data[0], 1));
    settings.setChangeImpossibleEvolutions(restoreState(data[0], 2));
    settings.setUpdateMoves(restoreState(data[0], 3));
    settings.setUpdateMovesLegacy(restoreState(data[0], 4));
    settings.setUpdateTypeEffectiveness(restoreState(data[0], 5));
    settings.setRandomizeTrainerNames(restoreState(data[0], 6));
    settings.setRandomizeTrainerClassNames(restoreState(data[0], 7));

    // sanity override
    if (settings.isUpdateMovesLegacy()
        && (settings.getRomHandlerFactory() instanceof Gen5RomHandler.Factory)) {
      // they probably don't want moves updated actually
      settings.setUpdateMovesLegacy(false);
      settings.setUpdateMoves(false);
    }

    settings.setBaseStatisticsMod(
        restoreEnum(BaseStatisticsMod.class, data[1],
            3, // UNCHANGED
            2, // SHUFFLE
            0, // RANDOM_FOLLOW_EVOLUTIONS
            1  // COMPLETELY_RANDOM
        ));
    settings.setAbilitiesMod(
        restoreEnum(AbilitiesMod.class, data[1],
            4, // UNCHANGED
            5  // RANDOMIZE
        ));
    settings.setAllowWonderGuard(!restoreState(data[1], 6));
    settings.setStandardizeEXPCurves(restoreState(data[1], 7));

    settings.setTypesMod(
        restoreEnum(TypesMod.class, data[2],
            2, // UNCHANGED
            0, // RANDOM_FOLLOW_EVOLUTIONS
            1  // COMPLETELY_RANDOM
        ));
    settings.setUseCodeTweaks(restoreState(data[2], 3));
    settings.setRaceMode(restoreState(data[2], 4));
    settings.setRandomizeHiddenHollows(restoreState(data[2], 5));
    settings.setAllowBrokenMoves(!restoreState(data[2], 6));
    settings.setLimitPokemon(restoreState(data[2], 7));

    settings.setMakeEvolutionsEasier(restoreState(data[3], 0));

    settings.setStartersMod(
        restoreEnum(StartersMod.class, data[4],
            2, // UNCHANGED
            0, // CUSTOM
            1, // COMPLETELY_RANDOM
            3  // RANDOM_WITH_TWO_EVOLUTIONS
        ));
    settings.setRandomizeStartersHeldItems(restoreState(data[4], 4));

    List<Pokemon> pokemon = settings.getRomHandler().getPokemon();
    settings.setCustomStarters(new Pokemon[] {
        restorePokemonIndex(data, 5, pokemon),
        restorePokemonIndex(data, 7, pokemon),
        restorePokemonIndex(data, 9, pokemon)
    });

    settings.setMovesetsMod(
        restoreEnum(MovesetsMod.class, data[11],
            2, // UNCHANGED
            1, // RANDOM_PREFER_SAME_TYPE
            0, // COMPLETELY_RANDOM
            3  // METRONOME_ONLY
        ));
    settings.setStartWithFourMoves(restoreState(data[11], 4));

    // changed 160
    settings.setTrainersMod(
        restoreEnum(TrainersMod.class, data[12],
            5, // UNCHANGED
            1, // RANDOM
            3  // TYPE_THEMED
        ));
    settings.setTrainersUsePokemonOfSimilarStrength(restoreState(data[12], 0));
    settings.setRivalCarriesStarterThroughout(restoreState(data[12], 2));
    settings.setTrainersMatchTypingDistribution(restoreState(data[12], 4));
    settings.setTrainersCanUseLegendaries(!restoreState(data[12], 6));
    settings.setTrainersEarlyWonderGuard(!restoreState(data[12], 7));

    settings.setWildPokemonMod(
        restoreEnum(WildPokemonMod.class, data[13],
            6, // UNCHANGED
            5, // RANDOM
            1, // AREA_MAPPING
            4  // GLOBAL_MAPPING
        ));
    settings.setWildPokemonRestrictionMod(
        getEnum(WildPokemonRestrictionMod.class,
            restoreState(data[13], 2), // NONE
            restoreState(data[14], 2), // SIMILAR_STRENGTH
            restoreState(data[13], 0), // CATCH_EM_ALL
            restoreState(data[13], 3)  // TYPE_THEME_AREAS
        ));
    settings.setUseTimeBasedEncounters(restoreState(data[13], 7));

    settings.setUseMinimumCatchRate(restoreState(data[14], 0));
    settings.setEncounterLegendaries(!restoreState(data[14], 1));
    settings.setRandomizeWildPokemonHeldItems(restoreState(data[14], 3));

    settings.setStaticPokemonMod(
        restoreEnum(StaticPokemonMod.class, data[15],
            0, // UNCHANGED
            1, // RANDOM_MATCHING
            2  // COMPLETELY_RANDOM
        ));

    settings.setTmsMod(
        restoreEnum(TMsMod.class, data[16],
            4, // UNCHANGED
            3  // RANDOM
        ));
    settings.setTmsHmsCompatibilityMod(
        restoreEnum(TMsHMsCompatibilityMod.class, data[16],
            2, // UNCHANGED
            1, // RANDOM_PREFER_TYPE
            0, // COMPLETELY_RANDOM
            7  // FULL
        ));
    settings.setTmLevelUpMoveSanity(restoreState(data[16], 5));
    settings.setKeepFieldMoves(restoreState(data[16], 6));

    settings.setMoveTutorMovesMod(
        restoreEnum(MoveTutorMovesMod.class, data[17],
            4, // UNCHANGED
            3  // RANDOM
        ));
    settings.setMoveTutorsCompatibilityMod(
        restoreEnum(MoveTutorsCompatibilityMod.class, data[17],
            2, // UNCHANGED
            1, // RANDOM_PREFER_TYPE
            0, // COMPLETELY_RANDOM
            7  // FULL
        ));
    settings.setTutorLevelUpMoveSanity(restoreState(data[17], 5));
    settings.setKeepFieldMoveTutors(restoreState(data[17], 6));

    // new 150
    settings.setInGameTradesMod(
        restoreEnum(InGameTradesMod.class, data[18],
            6, // UNCHANGED
            1, // RANDOMIZE_GIVEN
            0  // RANDOMIZE_GIVEN_AND_REQUESTED
        ));
    settings.setRandomizeInGameTradesItems(restoreState(data[18], 2));
    settings.setRandomizeInGameTradesIVs(restoreState(data[18], 3));
    settings.setRandomizeInGameTradesNicknames(restoreState(data[18], 4));
    settings.setRandomizeInGameTradesOTs(restoreState(data[18], 5));

    settings.setFieldItemsMod(
        restoreEnum(FieldItemsMod.class, data[19],
            2, // UNCHANGED
            1, // SHUFFLE
            0  // RANDOM
        ));

    // gen restrictions
    int genLimit = FileFunctions.readFullInt(data, 20);
    GenRestrictions restrictions = null;
    if (genLimit != 0) {
      restrictions = new GenRestrictions(genLimit);
      restrictions.limitToGen(settings.getRomHandler()
          .generationOfPokemon());
    }
    settings.setCurrentRestrictions(restrictions);

    int codeTweaks = FileFunctions.readFullInt(data, 24);
    codeTweaks = codeTweaks & settings.getRomHandler().codeTweaksAvailable();
    // Sanity override
    if (codeTweaks == 0) {
      settings.setUseCodeTweaks(false);
    }
    settings.setCurrentCodeTweaks(codeTweaks);

    return settings;
  }

  // TODO(kjs): GUI needs to be refactored not need this
  @Deprecated
  public static Settings fromStringWithRomHandler(String str, RomHandler romHandler) {
    return fromStringWithRomHandlerFactory(str, new RomHandler.Factory() {
      @Override
      public RomHandler create(Random ignored) {
        return romHandler;
      }
    });
  }

  //TODO(kjs): reorganize methods so setters follow getters

  public byte[] getTrainerClasses() {
    return trainerClasses;
  }

  public byte[] getTrainerNames() {
    return trainerNames;
  }

  public byte[] getNicknames() {
    return nicknames;
  }

  public RomHandler.Factory getRomHandlerFactory() {
    return romHandlerFactory;
  }

  public GenRestrictions getCurrentRestrictions() {
    return currentRestrictions;
  }

  public int getCurrentCodeTweaks() {
    return currentCodeTweaks;
  }

  public boolean isUpdateTypeEffectiveness() {
    return updateTypeEffectiveness;
  }

  public boolean isUpdateMoves() {
    return updateMoves;
  }

  public boolean isUpdateMovesLegacy() {
    return updateMovesLegacy;
  }

  public boolean isChangeImpossibleEvolutions() {
    return changeImpossibleEvolutions;
  }

  public boolean isMakeEvolutionsEasier() {
    return makeEvolutionsEasier;
  }

  public boolean isLowerCasePokemonNames() {
    return lowerCasePokemonNames;
  }

  public boolean isNationalDexAtStart() {
    return nationalDexAtStart;
  }

  public boolean isRaceMode() {
    return raceMode;
  }

  public boolean isRandomizeHiddenHollows() {
    return randomizeHiddenHollows;
  }

  public boolean isUseCodeTweaks() {
    return useCodeTweaks;
  }

  public boolean isAllowBrokenMoves() {
    return allowBrokenMoves;
  }

  public boolean isLimitPokemon() {
    return limitPokemon;
  }

  public BaseStatisticsMod getBaseStatisticsMod() {
    return baseStatisticsMod;
  }

  public boolean isStandardizeEXPCurves() {
    return standardizeEXPCurves;
  }

  public AbilitiesMod getAbilitiesMod() {
    return abilitiesMod;
  }

  public boolean isAllowWonderGuard() {
    return allowWonderGuard;
  }

  public StartersMod getStartersMod() {
    return startersMod;
  }

  public Pokemon[] getCustomStarters() {
    return customStarters;
  }

  public boolean isRandomizeStartersHeldItems() {
    return randomizeStartersHeldItems;
  }

  public TypesMod getTypesMod() {
    return typesMod;
  }

  public MovesetsMod getMovesetsMod() {
    return movesetsMod;
  }

  public boolean isStartWithFourMoves() {
    return startWithFourMoves;
  }

  public TrainersMod getTrainersMod() {
    return trainersMod;
  }

  public boolean isRivalCarriesStarterThroughout() {
    return rivalCarriesStarterThroughout;
  }

  public boolean isTrainersUsePokemonOfSimilarStrength() {
    return trainersUsePokemonOfSimilarStrength;
  }

  public boolean isTrainersMatchTypingDistribution() {
    return trainersMatchTypingDistribution;
  }

  public boolean isTrainersCanUseLegendaries() {
    return trainersCanUseLegendaries;
  }

  public boolean isTrainersEarlyWonderGuard() {
    return trainersEarlyWonderGuard;
  }

  public boolean isRandomizeTrainerNames() {
    return randomizeTrainerNames;
  }

  public boolean isRandomizeTrainerClassNames() {
    return randomizeTrainerClassNames;
  }

  public WildPokemonMod getWildPokemonMod() {
    return wildPokemonMod;
  }

  public WildPokemonRestrictionMod getWildPokemonRestrictionMod() {
    return wildPokemonRestrictionMod;
  }

  public boolean isUseTimeBasedEncounters() {
    return useTimeBasedEncounters;
  }

  public boolean isEncounterLegendaries() {
    return encounterLegendaries;
  }

  public boolean isUseMinimumCatchRate() {
    return useMinimumCatchRate;
  }

  public boolean isRandomizeWildPokemonHeldItems() {
    return randomizeWildPokemonHeldItems;
  }

  public StaticPokemonMod getStaticPokemonMod() {
    return staticPokemonMod;
  }

  public TMsMod getTmsMod() {
    return tmsMod;
  }

  public boolean isTmLevelUpMoveSanity() {
    return tmLevelUpMoveSanity;
  }

  public boolean isKeepFieldMoves() {
    return keepFieldMoves;
  }

  public TMsHMsCompatibilityMod getTmsHmsCompatibilityMod() {
    return tmsHmsCompatibilityMod;
  }

  public MoveTutorMovesMod getMoveTutorMovesMod() {
    return moveTutorMovesMod;
  }

  public boolean isTutorLevelUpMoveSanity() {
    return tutorLevelUpMoveSanity;
  }

  public boolean isKeepFieldMoveTutors() {
    return keepFieldMoveTutors;
  }

  public MoveTutorsCompatibilityMod getMoveTutorsCompatibilityMod() {
    return moveTutorsCompatibilityMod;
  }

  public InGameTradesMod getInGameTradesMod() {
    return inGameTradesMod;
  }

  public boolean isRandomizeInGameTradesNicknames() {
    return randomizeInGameTradesNicknames;
  }

  public boolean isRandomizeInGameTradesOTs() {
    return randomizeInGameTradesOTs;
  }

  public boolean isRandomizeInGameTradesIVs() {
    return randomizeInGameTradesIVs;
  }

  public boolean isRandomizeInGameTradesItems() {
    return randomizeInGameTradesItems;
  }

  public FieldItemsMod getFieldItemsMod() {
    return fieldItemsMod;
  }

  public Settings setTrainerClasses(final byte[] trainerClasses) {
    this.trainerClasses = trainerClasses;
    return this;
  }

  public Settings setTrainerNames(final byte[] trainerNames) {
    this.trainerNames = trainerNames;
    return this;
  }

  public Settings setNicknames(final byte[] nicknames) {
    this.nicknames = nicknames;
    return this;
  }

  public Settings setRomHandlerFactory(final RomHandler.Factory romHandlerFactory) {
    this.romHandlerFactory = romHandlerFactory;
    return this;
  }

  public Settings setCurrentRestrictions(final GenRestrictions currentRestrictions) {
    this.currentRestrictions = currentRestrictions;
    return this;
  }

  public Settings setCurrentCodeTweaks(final int currentCodeTweaks) {
    this.currentCodeTweaks = currentCodeTweaks;
    return this;
  }

  public Settings setUpdateTypeEffectiveness(final boolean updateTypeEffectiveness) {
    this.updateTypeEffectiveness = updateTypeEffectiveness;
    return this;
  }

  public Settings setUpdateMoves(final boolean updateMoves) {
    this.updateMoves = updateMoves;
    return this;
  }

  public Settings setUpdateMovesLegacy(final boolean updateMovesLegacy) {
    this.updateMovesLegacy = updateMovesLegacy;
    return this;
  }

  public Settings setChangeImpossibleEvolutions(final boolean changeImpossibleEvolutions) {
    this.changeImpossibleEvolutions = changeImpossibleEvolutions;
    return this;
  }

  public Settings setMakeEvolutionsEasier(final boolean makeEvolutionsEasier) {
    this.makeEvolutionsEasier = makeEvolutionsEasier;
    return this;
  }

  public Settings setLowerCasePokemonNames(final boolean lowerCasePokemonNames) {
    this.lowerCasePokemonNames = lowerCasePokemonNames;
    return this;
  }

  public Settings setNationalDexAtStart(final boolean nationalDexAtStart) {
    this.nationalDexAtStart = nationalDexAtStart;
    return this;
  }

  public Settings setRaceMode(final boolean raceMode) {
    this.raceMode = raceMode;
    return this;
  }

  public Settings setRandomizeHiddenHollows(final boolean randomizeHiddenHollows) {
    this.randomizeHiddenHollows = randomizeHiddenHollows;
    return this;
  }

  public Settings setUseCodeTweaks(final boolean useCodeTweaks) {
    this.useCodeTweaks = useCodeTweaks;
    return this;
  }

  public Settings setAllowBrokenMoves(final boolean allowBrokenMoves) {
    this.allowBrokenMoves = allowBrokenMoves;
    return this;
  }

  public Settings setLimitPokemon(final boolean limitPokemon) {
    this.limitPokemon = limitPokemon;
    return this;
  }

  public Settings setBaseStatisticsMod(final BaseStatisticsMod baseStatisticsMod) {
    this.baseStatisticsMod = baseStatisticsMod;
    return this;
  }

  public Settings setBaseStatisticsMod(boolean... bools) {
    return setBaseStatisticsMod(getEnum(BaseStatisticsMod.class, bools));
  }

  public Settings setStandardizeEXPCurves(final boolean standardizeEXPCurves) {
    this.standardizeEXPCurves = standardizeEXPCurves;
    return this;
  }

  public Settings setAbilitiesMod(final AbilitiesMod abilitiesMod) {
    this.abilitiesMod = abilitiesMod;
    return this;
  }

  public Settings setAbilitiesMod(boolean... bools) {
    return setAbilitiesMod(getEnum(AbilitiesMod.class, bools));
  }

  public Settings setAllowWonderGuard(final boolean allowWonderGuard) {
    this.allowWonderGuard = allowWonderGuard;
    return this;
  }

  public Settings setStartersMod(final StartersMod startersMod) {
    this.startersMod = startersMod;
    return this;
  }

  public Settings setStartersMod(boolean... bools) {
    return setStartersMod(getEnum(StartersMod.class, bools));
  }

  public Settings setCustomStarters(final Pokemon[] customStarters) {
    this.customStarters = customStarters;
    return this;
  }

  public Settings setRandomizeStartersHeldItems(final boolean randomizeStartersHeldItems) {
    this.randomizeStartersHeldItems = randomizeStartersHeldItems;
    return this;
  }

  public Settings setTypesMod(final TypesMod typesMod) {
    this.typesMod = typesMod;
    return this;
  }

  public Settings setTypesMod(boolean... bools) {
    return setTypesMod(getEnum(TypesMod.class, bools));
  }

  public Settings setMovesetsMod(final MovesetsMod movesetsMod) {
    this.movesetsMod = movesetsMod;
    return this;
  }

  public Settings setMovesetsMod(boolean... bools) {
    return setMovesetsMod(getEnum(MovesetsMod.class, bools));
  }

  public Settings setStartWithFourMoves(final boolean startWithFourMoves) {
    this.startWithFourMoves = startWithFourMoves;
    return this;
  }

  public Settings setTrainersMod(final TrainersMod trainersMod) {
    this.trainersMod = trainersMod;
    return this;
  }

  public Settings setTrainersMod(boolean... bools) {
    return setTrainersMod(getEnum(TrainersMod.class, bools));
  }

  public Settings setRivalCarriesStarterThroughout(final boolean rivalCarriesStarterThroughout) {
    this.rivalCarriesStarterThroughout = rivalCarriesStarterThroughout;
    return this;
  }

  public Settings setTrainersUsePokemonOfSimilarStrength(final boolean trainersUsePokemonOfSimilarStrength) {
    this.trainersUsePokemonOfSimilarStrength = trainersUsePokemonOfSimilarStrength;
    return this;
  }

  public Settings setTrainersMatchTypingDistribution(final boolean trainersMatchTypingDistribution) {
    this.trainersMatchTypingDistribution = trainersMatchTypingDistribution;
    return this;
  }

  public Settings setTrainersCanUseLegendaries(final boolean trainersCanUseLegendaries) {
    this.trainersCanUseLegendaries = trainersCanUseLegendaries;
    return this;
  }

  public Settings setTrainersEarlyWonderGuard(final boolean trainersEarlyWonderGuard) {
    this.trainersEarlyWonderGuard = trainersEarlyWonderGuard;
    return this;
  }

  public Settings setRandomizeTrainerNames(final boolean randomizeTrainerNames) {
    this.randomizeTrainerNames = randomizeTrainerNames;
    return this;
  }

  public Settings setRandomizeTrainerClassNames(final boolean randomizeTrainerClassNames) {
    this.randomizeTrainerClassNames = randomizeTrainerClassNames;
    return this;
  }

  public Settings setWildPokemonMod(final WildPokemonMod wildPokemonMod) {
    this.wildPokemonMod = wildPokemonMod;
    return this;
  }

  public Settings setWildPokemonMod(boolean... bools) {
    return setWildPokemonMod(getEnum(WildPokemonMod.class, bools));
  }

  public Settings setWildPokemonRestrictionMod(final WildPokemonRestrictionMod wildPokemonRestrictionMod) {
    this.wildPokemonRestrictionMod = wildPokemonRestrictionMod;
    return this;
  }

  public Settings setWildPokemonRestrictionMod(boolean... bools) {
    return setWildPokemonRestrictionMod(getEnum(WildPokemonRestrictionMod.class, bools));
  }

  public Settings setUseTimeBasedEncounters(final boolean useTimeBasedEncounters) {
    this.useTimeBasedEncounters = useTimeBasedEncounters;
    return this;
  }

  public Settings setEncounterLegendaries(final boolean encounterLegendaries) {
    this.encounterLegendaries = encounterLegendaries;
    return this;
  }

  public Settings setUseMinimumCatchRate(final boolean useMinimumCatchRate) {
    this.useMinimumCatchRate = useMinimumCatchRate;
    return this;
  }

  public Settings setRandomizeWildPokemonHeldItems(final boolean randomizeWildPokemonHeldItems) {
    this.randomizeWildPokemonHeldItems = randomizeWildPokemonHeldItems;
    return this;
  }

  public Settings setStaticPokemonMod(final StaticPokemonMod staticPokemonMod) {
    this.staticPokemonMod = staticPokemonMod;
    return this;
  }

  public Settings setStaticPokemonMod(boolean... bools) {
    return setStaticPokemonMod(getEnum(StaticPokemonMod.class, bools));
  }

  public Settings setTmsMod(final TMsMod tmsMod) {
    this.tmsMod = tmsMod;
    return this;
  }

  public Settings setTmsMod(boolean... bools) {
    return setTmsMod(getEnum(TMsMod.class, bools));
  }

  public Settings setTmLevelUpMoveSanity(final boolean tmLevelUpMoveSanity) {
    this.tmLevelUpMoveSanity = tmLevelUpMoveSanity;
    return this;
  }

  public Settings setKeepFieldMoves(final boolean keepFieldMoves) {
    this.keepFieldMoves = keepFieldMoves;
    return this;
  }

  public Settings setTmsHmsCompatibilityMod(final TMsHMsCompatibilityMod tmsHmsCompatibilityMod) {
    this.tmsHmsCompatibilityMod = tmsHmsCompatibilityMod;
    return this;
  }

  public Settings setTmsHmsCompatibilityMod(boolean... bools) {
    return setTmsHmsCompatibilityMod(getEnum(TMsHMsCompatibilityMod.class, bools));
  }

  public Settings setMoveTutorMovesMod(final MoveTutorMovesMod moveTutorMovesMod) {
    this.moveTutorMovesMod = moveTutorMovesMod;
    return this;
  }

  public Settings setMoveTutorMovesMod(boolean... bools) {
    return setMoveTutorMovesMod(getEnum(MoveTutorMovesMod.class, bools));
  }

  public Settings setTutorLevelUpMoveSanity(final boolean tutorLevelUpMoveSanity) {
    this.tutorLevelUpMoveSanity = tutorLevelUpMoveSanity;
    return this;
  }

  public Settings setKeepFieldMoveTutors(final boolean keepFieldMoveTutors) {
    this.keepFieldMoveTutors = keepFieldMoveTutors;
    return this;
  }

  public Settings setMoveTutorsCompatibilityMod(final MoveTutorsCompatibilityMod moveTutorsCompatibilityMod) {
    this.moveTutorsCompatibilityMod = moveTutorsCompatibilityMod;
    return this;
  }

  public Settings setMoveTutorsCompatibilityMod(boolean... bools) {
    return setMoveTutorsCompatibilityMod(getEnum(MoveTutorsCompatibilityMod.class, bools));
  }

  public Settings setInGameTradesMod(final InGameTradesMod inGameTradesMod) {
    this.inGameTradesMod = inGameTradesMod;
    return this;
  }

  public Settings setInGameTradesMod(boolean... bools) {
    return setInGameTradesMod(getEnum(InGameTradesMod.class, bools));
  }

  public Settings setRandomizeInGameTradesNicknames(final boolean randomizeInGameTradesNicknames) {
    this.randomizeInGameTradesNicknames = randomizeInGameTradesNicknames;
    return this;
  }

  public Settings setRandomizeInGameTradesOTs(final boolean randomizeInGameTradesOTs) {
    this.randomizeInGameTradesOTs = randomizeInGameTradesOTs;
    return this;
  }

  public Settings setRandomizeInGameTradesIVs(final boolean randomizeInGameTradesIVs) {
    this.randomizeInGameTradesIVs = randomizeInGameTradesIVs;
    return this;
  }

  public Settings setRandomizeInGameTradesItems(final boolean randomizeInGameTradesItems) {
    this.randomizeInGameTradesItems = randomizeInGameTradesItems;
    return this;
  }

  public Settings setFieldItemsMod(final FieldItemsMod fieldItemsMod) {
    this.fieldItemsMod = fieldItemsMod;
    return this;
  }

  public Settings setFieldItemsMod(boolean... bools) {
    return setFieldItemsMod(getEnum(FieldItemsMod.class, bools));
  }

  private RomHandler getRomHandler() {
    // NB: We don't need to use RandomSource because the Settings does not
    // call randomized functions.
    return romHandlerFactory.create(new Random());
  }

  private static void writePokemonIndex(ByteArrayOutputStream out, Pokemon pokemon) {
    int index = pokemon.number - 1;
    out.write(index & 0xFF);
    out.write((index >> 8) & 0xFF);
  }

  private static Pokemon restorePokemonIndex(byte[] data, int offset, List<Pokemon> pokemon) {
    int index = ((data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8)) + 1;
    if (index > pokemon.size()) {
      return pokemon.get(pokemon.size() - 1);
    } else {
      return pokemon.get(index);
    }
  }

  private static int makeByteSelected(boolean... bools) {
    if (bools.length > 8) {
      throw new IllegalArgumentException("Can't set more than 8 bits in a byte!");
    }

    int initial = 0;
    int state = 1;
    for (boolean b : bools) {
      initial |= b ? state : 0;
      state *= 2;
    }
    return initial;
  }

  private static boolean restoreState(byte b, int index) {
    if (index >= 8) {
      throw new IllegalArgumentException("Can't read more than 8 bits from a byte!");
    }

    int value = b & 0xFF;
    return ((value >> index) & 0x01) == 0x01;
  }

  private static void writeFullInt(ByteArrayOutputStream out, int checksum)
      throws IOException {
    byte[] crc = ByteBuffer.allocate(4).putInt(checksum).array();
    out.write(crc);
  }

  public static <E extends Enum<E>> E restoreEnum(Class<E> clazz, byte b, int... indices) {
    boolean[] bools = new boolean[indices.length];
    for (int i : indices) {
      bools[i] = restoreState(b, i);
    }
    return getEnum(clazz, bools);
  }

  @SuppressWarnings("unchecked")
  public static <E extends Enum<E>> E getEnum(Class<E> clazz, boolean... bools) {
    int index = getSetEnum(clazz.getSimpleName(), bools);
    try {
      return ((E[]) clazz.getMethod("values").invoke(null))[index];
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Unable to parse enum of type %s", clazz.getSimpleName()));
    }
  }

  private static int getSetEnum(String type, boolean... bools) {
    int index = -1;
    for (int i = 0; i < bools.length; i++) {
      if (bools[i]) {
        if (index >= 0) {
          throw new IllegalStateException(
              String.format("Only one value for %s may be chosen!", type));
        }
        index = i;
      }
    }
    return index;
  }


  private static void checkChecksum(byte[] data) {
    // Check the checksum
    ByteBuffer buf = ByteBuffer.allocate(4).put(data, data.length - 16, 4);
    buf.rewind();
    int crc = buf.getInt();

    CRC32 checksum = new CRC32();
    checksum.update(data, 0, data.length - 16);

    if ((int) checksum.getValue() != crc) {
      throw new IllegalArgumentException("Malformed input string");
    }
  }

  /**
   * Parses a settings string from an old randomizer version and updates it
   * to be compatible with the currently running randomizer version.
   */
  private static class LegacyParser {

    // TODO(kjs): update comments to use new field names

    private byte[] dataBlock;
    private int actualDataLength;

    /**
     * Updates an old settings string.
     *
     * @param oldVersion The VERSION used to generate the given string.
     * @param string The outdated settings string.
     * @return The updated settings string to be applied.
     */
    public String update(int oldVersion, String string) {
      byte[] data = DatatypeConverter.parseBase64Binary(string);
      this.dataBlock = new byte[100];
      this.actualDataLength = data.length;
      System.arraycopy(data, 0, this.dataBlock, 0, this.actualDataLength);

      // new field values here are written as bitwise ORs
      // this is slightly slower in execution, but it makes it clearer
      // just what values we actually want to set
      // bit fields 1 2 3 4 5 6 7 8
      // are values 0x01 0x02 0x04 0x08 0x10 0x20 0x40 0x80

      // versions prior to 120 didn't have quick settings file,
      // they're just included here for completeness' sake

      // versions < 102: add abilities set to unchanged
      if (oldVersion < 102) {
        dataBlock[1] |= 0x10;
      }

      // versions < 110: add move tutor byte (set both to unchanged)
      if (oldVersion < 110) {
        insertExtraByte(15, (byte) (0x04 | 0x10));
      }

      // version 110-111 no change (only added trainer names/classes to preset
      // files, and some checkboxes which it is safe to leave as off)

      // 111-112 no change (another checkbox we leave as off)

      // 112-120 no change (only another checkbox)

      // 120-150 new features
      if (oldVersion < 150) {
        // trades and field items: both unchanged
        insertExtraByte(16, (byte) (0x40));
        insertExtraByte(17, (byte) (0x04));
        // add a fake checksum for nicknames at the very end of the data,
        // we can leave it at 0
        actualDataLength += 4;
      }

      // 150-160 lots of re-org etc
      if (oldVersion < 160) {
        // byte 0:
        // copy "update moves" to "update legacy moves"
        // move the other 3 fields after it up one
        int firstByte = dataBlock[0] & 0xFF;
        int updateMoves = firstByte & 0x08;
        int laterFields = firstByte & (0x10 | 0x20 | 0x40);
        dataBlock[0] = (byte) ((firstByte & (0x01 | 0x02 | 0x04 | 0x08))
            | (updateMoves << 1) | (laterFields << 1));

        // byte 1:
        // leave as is (don't turn on exp standardization)

        // byte 2:
        // retrieve values of bw exp patch & held items
        // code tweaks keeps the same value as bw exp patch had
        // but turn held items off (it got replaced by pokelimit)
        int hasBWPatch = (dataBlock[2] & 0x08) >> 3;
        int hasHeldItems = (dataBlock[2] & 0x80) >> 7;
        dataBlock[2] &= (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40);

        // byte 3:
        // turn on starter held items if held items checkbox was on
        if (hasHeldItems > 0) {
          dataBlock[3] |= 0x10;
        }

        // byte 4-9 are starters
        // byte 10 adds "4 moves" but we leave it off

        // byte 11:
        // pull out value of WP no legendaries
        // replace it with TP no early shedinja
        // also get WP catch rate value
        int wpNoLegendaries = (dataBlock[11] & 0x80) >> 7;
        int tpNoEarlyShedinja = (dataBlock[13] & 0x10) >> 4;
        int wpCatchRate = (dataBlock[13] & 0x08) >> 3;
        dataBlock[11] = (byte) ((dataBlock[11] & (0x01 | 0x02 | 0x04 | 0x08
            | 0x10 | 0x20 | 0x40)) | (tpNoEarlyShedinja << 7));

        // byte 12 unchanged

        // insert a new byte for "extra" WP stuff
        // include no legendaries & catch rate
        // also include WP held items if overall held items box was on
        // leave similar strength off, there's a bugfix a little later on...
        insertExtraByte(
            13,
            (byte) ((wpCatchRate) | (wpNoLegendaries << 1) | (hasHeldItems << 3)));

        // new byte 14 (was 13 in 150):
        // switch off bits 4 and 5 (were for catch rate & no early shedinja)
        dataBlock[14] &= 0x07;

        // the rest of the settings bytes are unchanged
        // but we need to add the fields for pokemon limit & code tweaks

        // no pokemon limit
        insertIntField(19, 0);

        // only possible code tweak = bw exp
        insertIntField(23, hasBWPatch);
      }

      // 160 bug:
      // check if all of the WPAdditionalRule bitfields are unset
      // (None, Type Themed, Catch Em All)
      // if they are all unset, switch "similar strength" on
      if ((dataBlock[12] & (0x01 | 0x04 | 0x08)) == 0) {
        dataBlock[13] |= 0x04;
      }

      // 160 to 161: no change
      // the only changes were in implementation, which broke presets, but
      // leaves settings files the same

      // 161 to 162:
      // some added fields to tm/move tutors that we can leave blank
      // more crucially: a new general options byte @ offset 3
      // set it to all off by default
      if (oldVersion < 162) {
        insertExtraByte(3, (byte) 0);
      }

      // fix checksum
      CRC32 checksum = new CRC32();
      checksum.update(dataBlock, 0, actualDataLength - 16);

      // convert crc32 to int bytes
      byte[] crcBuf = ByteBuffer.allocate(4)
          .putInt((int) checksum.getValue()).array();
      System.arraycopy(crcBuf, 0, dataBlock, actualDataLength - 16, 4);

      // have to make a new byte array to convert to base64
      byte[] finalString = new byte[actualDataLength];
      System.arraycopy(dataBlock, 0, finalString, 0, actualDataLength);
      return DatatypeConverter.printBase64Binary(finalString);
    }

    /**
     * Insert a 4-byte int field in the data block at the given position. Shift
     * everything else up. Do nothing if there's no room left (should never
     * happen)
     *
     * @param position
     *            The offset to add the field
     * @param value
     *            The value to give to the field
     */
    private void insertIntField(int position, int value) {
      if (actualDataLength + 4 > dataBlock.length) {
        // can't do
        return;
      }
      for (int j = actualDataLength; j > position + 3; j--) {
        dataBlock[j] = dataBlock[j - 4];
      }
      byte[] valueBuf = ByteBuffer.allocate(4).putInt(value).array();
      System.arraycopy(valueBuf, 0, dataBlock, position, 4);
      actualDataLength += 4;
    }

    /**
     * Insert a byte-field in the data block at the given position. Shift
     * everything else up. Do nothing if there's no room left (should never
     * happen)
     *
     * @param position
     *            The offset to add the field
     * @param value
     *            The value to give to the field
     */
    private void insertExtraByte(int position, byte value) {
      if (actualDataLength == dataBlock.length) {
        // can't do
        return;
      }
      for (int j = actualDataLength; j > position; j--) {
        dataBlock[j] = dataBlock[j - 1];
      }
      dataBlock[position] = value;
      actualDataLength++;
    }
  }
}