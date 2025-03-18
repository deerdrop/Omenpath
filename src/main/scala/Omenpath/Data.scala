package Omenpath

object Data:
  val newSeason: Int = 7
  val minCardCount: Int = 1600
  val minSetCount: Int = 7
  val scryfallFetchHeaders = Map("User-Agent" -> "OmenpathSetApp/0.1", "Accept" -> "*/*")
  lazy val ModernSets = Vector("TDM", "DFT", "FDN", "DSK", "BLB", "OTJ", "MKM", "LCI", "WOE", "MAT", "MOM", "ONE", "BRO", "DMU", "SNC", "NEO", "VOW", "MID", "AFR", "STX", "KHM", "ZNR", "M21", "IKO", "THB", "ELD", "M20", "WAR", "RNA", "GRN", "M19", "DOM", "RIX", "XLN", "HOU", "AKH", "AER", "KLD", "EMN", "SOI", "OGW", "BFZ", "ORI", "DTK", "FRF", "KTK", "M15", "JOU", "BNG", "THS", "M14", "DGM", "GTC", "RTR", "M13", "AVR", "DKA", "ISD", "M12", "NPH", "MBS", "SOM", "M11", "ROE", "WWK", "ZEN", "M10", "ARB", "CON", "ALA", "EVE", "SHM", "MOR", "LRW", "10E", "FUT", "PLC", "TSP", "CSP", "DIS", "GPT", "RAV", "9ED", "SOK", "BOK", "CHK", "5DN", "DST", "MRD", "8ED")
  lazy val CoreSets = Vector("FDN", "M21", "M20", "M19", "M15", "M14", "M13", "M12", "M11", "M10", "10E", "9ED", "8ED")
  lazy val SeasonRecord = Map(
    "Season 1" -> Vector("9ED", "M21", "THS", "ALA", "SHM", "ONE", "ROE"),
    "Season 2" -> Vector("RNA", "KLD", "DTK", "EVE", "SOK", "M12", "M15", "M20"),
    "Season 3" -> Vector("STX", "JOU", "CHK", "ROE", "M11", "WAR", "OTJ"),
    "Season 4" -> Vector("XLN", "SOI", "OGW", "SOM", "ALA", "TSP", "M10"),
    "Season 5" -> Vector("ELD", "VOW", "AFR", "CSP", "PLC", "AFR", "DGM", "AER"),
    "Season 6" -> Vector("NEO", "THB", "RNA", "DKA", "CON", "LRW", "BOK", "ORI"),
  )