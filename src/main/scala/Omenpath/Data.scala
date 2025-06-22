package Omenpath

object Data:
  val newSeason: Int = 8                      // Number of the Season being rolled.
  val minCardCount: Int = 1500                // Minimum number of unique cards for a valid Season.
  val minSetCount: Int = 8                    // Minimum number of sets for a valid Season.
  val lockoutSeasons: Int = 2                 // Number of Seasons for which a set is not allowed to reappear.
  val scryfallFetchHeaders = Map("User-Agent" -> "OmenpathSetApp/0.1", "Accept" -> "*/*")
  // This string of functions creates a LazyList of ticks at which slots should be stopped.
  // It's formatted this way to allow direct edits to the intervals between the slots stopping, in ticks, and letting the compiler math out the final ticks.
  // padTo allows us to hit a theoretical 30 slots, which should be absurdly high enough. Notable that scanLeft, while preserving Laziness, still takes time to complete based on the list's total length.
  val slotKeyFrames = LazyList(25, 18, 21, 25, 29, 33, 37).padTo(30, 42).scanLeft(0)(_ + _).drop(1)
  val slotTimeUntilStop: Int = 30             // Number of ticks for a slot to stop spinning from full speed.
  lazy val ModernSets = Vector("TDM", "DFT", "FDN", "DSK", "BLB", "OTJ", "MKM", "LCI", "WOE", "MAT", "MOM", "ONE", "BRO", "DMU", "SNC", "NEO", "VOW", "MID", "AFR", "STX", "KHM", "ZNR", "M21", "IKO", "THB", "ELD", "M20", "WAR", "RNA", "GRN", "M19", "DOM", "RIX", "XLN", "HOU", "AKH", "AER", "KLD", "EMN", "SOI", "OGW", "BFZ", "ORI", "DTK", "FRF", "KTK", "M15", "JOU", "BNG", "THS", "M14", "DGM", "GTC", "RTR", "M13", "AVR", "DKA", "ISD", "M12", "NPH", "MBS", "SOM", "M11", "ROE", "WWK", "ZEN", "M10", "ARB", "CON", "ALA", "EVE", "SHM", "MOR", "LRW", "10E", "FUT", "PLC", "TSP", "CSP", "DIS", "GPT", "RAV", "9ED", "SOK", "BOK", "CHK", "5DN", "DST", "MRD", "8ED")
  lazy val CoreSets = Vector("FDN", "M21", "M20", "M19", "M15", "M14", "M13", "M12", "M11", "M10", "10E", "9ED", "8ED")
  lazy val SeasonRecord = Map(
    "Season 1" -> Vector("9ED", "M21", "THS", "ALA", "SHM", "ONE", "ROE"),
    "Season 2" -> Vector("RNA", "KLD", "DTK", "EVE", "SOK", "M12", "M15", "M20"),
    "Season 3" -> Vector("STX", "JOU", "CHK", "ROE", "M11", "WAR", "OTJ"),
    "Season 4" -> Vector("XLN", "SOI", "OGW", "SOM", "ALA", "TSP", "M10"),
    "Season 5" -> Vector("ELD", "VOW", "AFR", "CSP", "PLC", "AFR", "DGM", "AER"),
    "Season 6" -> Vector("NEO", "THB", "RNA", "DKA", "CON", "LRW", "BOK", "ORI"),
    "Season 7" -> Vector("STX", "RAV", "WWK", "M14", "THS", "DIS", "BLB", "HOU")
  )