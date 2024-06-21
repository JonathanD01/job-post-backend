package no.jobbscraper.jobpostapi.util;

import java.util.List;
import java.util.Map;

public class GeoUtil {


    // TODO Replace map with more up to date values
    // !!! Keys should have no spaces !!!
    public static final Map<String, List<String>> municipalityCityMap = Map.ofEntries(
            Map.entry("oslo", List.of("Oslo")),
            Map.entry("akershus", List.of("Askim", "Drøbak", "Halden", "Lillestrøm", "Moss", "Ski", "Ås")),
            Map.entry("buskerud", List.of("Drammen", "Hokksund", "Kongsberg", "Mjøndalen", "Øvre Eiker")),
            Map.entry("østfold", List.of("Fredrikstad", "Halden", "Moss", "Sarpsborg")),
            Map.entry("innlandet", List.of("Hamar", "Gjøvik", "Lillehammer", "Kongsvinger", "Elverum", "Alvdal", "Brumunddal", "Fagernes", "Otta", "Rena", "Trysil")),
            Map.entry("vestfoldogtelemark", List.of("Tønsberg", "Sandefjord", "Larvik", "Skien", "Porsgrunn", "Brevik", "Flekkefjord", "Grimstad", "Kragerø", "Notodden", "Stathelle")),
            Map.entry("agder", List.of("Kristiansand", "Arendal", "Grimstad", "Mandal", "Flekkefjord", "Risør", "Tvedestrand")),
            Map.entry("rogaland", List.of("Stavanger", "Sandnes", "Haugesund", "Bryne", "Egersund", "Eigersund")),
            Map.entry("vestland", List.of("Bergen", "Florø", "Førde", "Kvam", "Leirvik", "Måløy", "Sogndal", "Stryn", "Ulsteinvik", "Voss")),
            Map.entry("møreogromsdal", List.of("Ålesund", "Kristiansund", "Molde", "Ulsteinvik")),
            Map.entry("trøndelag", List.of("Brekstad", "Heimdal", "Levanger", "Namsos", "Steinkjer", "Stjørdalshalsen", "Trondheim")),
            Map.entry("nordland", List.of("Bodø", "Mo i Rana", "Narvik", "Sandnessjøen", "Svolvær")),
            Map.entry("tromsogfinmark", List.of("Alta", "Hammerfest", "Harstad", "Kirkenes", "Tromsø", "Vadsø"))
    );

}
