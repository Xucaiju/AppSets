package xcj.appsets.model

data class TodayAppType(var typeMusic:String?="",
                        var typeGame:String?="",
                        var typeTools:String?="",
                        var typeFamily:String?="",
                        var typeKnowledge:String?="",
                        var typePhotography:String?="",
                        var typeShopping:String?="",
                        var typeIM:String?="",
                        var typeNews:String?="",
                        var typeFreetime:String?="",
                        var typeHealthy:String?="",
                        var typeOffice:String?="",
                        var typeParentChild:String?=""
) {
    override fun toString(): String {
        return "TodayAppType(typeMusic=$typeMusic, typeGame=$typeGame, typeTools=$typeTools, typeFamily=$typeFamily, typeKnowledge=$typeKnowledge, typePhotography=$typePhotography, typeShopping=$typeShopping, typeIM=$typeIM, typeNews=$typeNews, typeFreetime=$typeFreetime, typeHealthy=$typeHealthy, typeOffice=$typeOffice, typeParentChild=$typeParentChild)"
    }
}