package ca.cmpt213.as2;
import com.google.gson.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;

import java.util.*;
import java.io.*;

import static java.lang.System.exit;

/**
 * Collecting data from json files and output data to csv files
 * Organize Tokimon in teams
 */
public class TokimonProcessor {
    private static String name;
    private static String id;
    private static String comment;
    private static String extra_comment;
    private static float score;

    private static List<File> jsonFilesList = new ArrayList<>();
    private static List<Tokimon> tokimonList = new ArrayList<>();
    private static List<TokimonTeam> teamList = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException {
         //Receive input arguments from user
        if (args.length != 2) {
            printErrorMgs("[ERROR] Incorrect number of inputs. Please re-submit 2 directories:" + "/n"
                            + "1. Path to input directory files (.json files)"+ "/n"
                            + "2. Path to output directory file (.cvs file)");
        }
        String inputPath = args[0];
        String outputPath = args[1];


        // Collecting data
        if (!isFolderExist(inputPath))
            printErrorMgs("[ERROR] Incorrect input path");
        jsonFilesRecursive(inputPath);
        if (jsonFilesList.size() == 0)
            printErrorMgs("[ERROR] No json files found");

        for(File jsonFile : jsonFilesList) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);
            JsonArray team = jsonObject.getAsJsonArray("team");
            extra_comment = jsonObject.get("extra_comments").getAsString();

            String feedbackFromTokiID = "";
            for (JsonElement each : team) {
                JsonObject eachFeedback = (JsonObject) each;
                if (!doesJFileHasEnoughFields(eachFeedback,3))
                    printErrorMgs("[ERROR] Json file " + jsonFile.getName() + " does not have enough required fields");

                JsonObject compatibility = eachFeedback.get("compatibility").getAsJsonObject();
                if (!doesJFileHasEnoughFields(compatibility,2))
                    printErrorMgs("[ERROR] Json file " + jsonFile.getName() + " does not have enough required fields");

                name = eachFeedback.get("name").getAsString();
                id = eachFeedback.get("id").getAsString();
                comment = compatibility.get("comment").getAsString();
                score = compatibility.get("score").getAsFloat();
                if (!isScoreValid(score))
                    printErrorMgs("[ERROR] Json file " + jsonFile.getName() + " has invalid score value");

                if (each == team.get(0)) {
                    TokimonFeedback selfFeedback = new TokimonFeedback(name,id,score,comment,extra_comment);
                    Tokimon feedbackFromToki = new Tokimon(name,id,selfFeedback,true);
                    if(!isTokimonAlreadyExists(feedbackFromToki))
                        tokimonList.add(feedbackFromToki);
                    feedbackFromTokiID = feedbackFromToki.getID();
                }
                else {
                    TokimonFeedback otherFeedbacks = new TokimonFeedback(name,id,score,comment,"");
                    Tokimon feedbackToToki = new Tokimon(name,id,otherFeedbacks,false);
                    if (!isTokimonAlreadyExists(feedbackToToki))
                        tokimonList.add(feedbackToToki);
                    for (Tokimon eachToki : tokimonList) {
                        if (eachToki.matchID(feedbackFromTokiID)) {
                            eachToki.addOtherFeedbacks(otherFeedbacks);
                            eachToki.setIsFeedbackSubmitted(true);
                            eachToki.getSelfFeedback().setExtra_comment(extra_comment);
                        }
                    }
                }
            }
        }

        // Categorize Tokimons in teams
        organizeTeam(tokimonList);
//        for (TokimonTeam each:teamList) {
//            each.print();
//            System.out.println();
//        }
//
        for (Tokimon each:tokimonList) {
            each.print();
        }
        System.out.println(teamList.size());
        if (!doesAllSubmitJsonFile())
            printErrorMgs(" does not submit a Json file");
        if (!doesAllSubmitEnoughFeedback())
            printErrorMgs(" does not submit enough feedbacks for other team mates");


        // Outputting the data
        generateCSVFile(outputPath);


    }

    private static void generateCSVFile(String outputPath) {
        File outputFolder = new File(outputPath);
        File generatedFile = new File(outputFolder.getAbsolutePath() + File.separator + "team_info.cvs");
        if (!isFolderExist(outputPath))
            printErrorMgs("[ERROR] Incorrect output path");

        PrintWriter printer;
        try {
            printer = new PrintWriter(generatedFile);
            printer.println("Team#,From Toki, To Toki,Score,Comment,,Extra");

            int teamNumber = 0;
            for (int i = 0; i < teamList.size(); i++) {
                teamNumber++;
                printer.println(Integer.toString(teamNumber));
                TokimonTeam eachTeam = teamList.get(i);
                eachTeam.sort();
                for (Tokimon eachToki : eachTeam.getTeamMember()) {
//                    eachToki.sort();
                    printer.printf("," + eachToki.getID() + "-" + eachToki.getSelfFeedback().getScore()
                            + "," + eachToki.getSelfFeedback().getComment() + ","
                            + "," + eachToki.getSelfFeedback().getExtra_comment());
                    for (TokimonFeedback eachFeedback : eachToki.getOtherFeedbacks()) {
                        printer.println("," + eachToki.getID() + "," + eachFeedback.getID() + ","
                                + eachFeedback.getScore() + "" + eachFeedback.getComment()
                                + ",,");
                    }
                }
            }
            printer.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    private static boolean isFolderExist(String path) {
        File folder = new File(path);
        if (!folder.exists())
            return false;
        else if (!folder.isDirectory())
            return false;
        return true;
    }

    private static void jsonFilesRecursive(String inputPath) {
        File inputFolder = new File(inputPath);
        File[] jsonFiles = inputFolder.listFiles(new FileFilter(){
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".json");
            }
        } );
        if (jsonFiles != null && jsonFiles.length > 0) {
            jsonFilesList.addAll(new ArrayList<File>(Arrays.asList(jsonFiles)));
        }
        for (File file : inputFolder.listFiles())
            if (file.isDirectory())
                jsonFilesRecursive(file.getAbsolutePath());
    }

    public static boolean doesJFileHasEnoughFields(JsonObject jsonObject, int requiredFields) {
        if (jsonObject == null || jsonObject.size() < requiredFields)
            return false;
        return true;
    }

    public static boolean isScoreValid(float score) {
        if (score < 0)
            return false;
        return true;
    }

    private static void organizeTeam(List<Tokimon> tokimonList) {
        for (Tokimon each : tokimonList) {
            TokimonTeam team = findExistingTeamForToki(each);
            if (team == null)
                teamList.add(new TokimonTeam(each));
            else
                team.addMember(each);
        }
    }

    private static TokimonTeam findExistingTeamForToki(Tokimon tokimon) {
        TokimonTeam team = null;
        for (TokimonTeam each : teamList) {
            if (each.containsTokimon(tokimon.getID()))
                printErrorMgs("Tokimon " + tokimon.getID() + " already belongs to another group");
            else if (each.containsInFeedback(tokimon.getID()))
                team = each;
        }
        return team;
    }

    private static boolean doesAllSubmitJsonFile() {
        for (TokimonTeam eachTeam : teamList) {
            for (Tokimon eachToki : eachTeam.getTeamMember())
                if (eachToki.getIsFeedbackSubmitted() != true) {
                    System.out.print("[ERROR] Toki " + eachToki.getID());
                    return false;
                }
        }
        return true;
    }

    private static boolean doesAllSubmitEnoughFeedback() {
        for (TokimonTeam eachTeam : teamList) {
            for (Tokimon eachToki : eachTeam.getTeamMember())
                if (eachToki.getOtherFeedbacks().size() != (eachTeam.getTeamMember().size()-1)) {
                    System.out.println(eachTeam.getTeamMember().size()-1);
                    System.out.print("[ERROR] Tokimon " + eachToki.getID() + eachToki.getOtherFeedbacks().size());
                    return false;
                }
        }
        return true;
    }

    private static boolean isTokimonAlreadyExists(Tokimon feedbackToToki) {
        if (tokimonList == null)
            return  false;
        for (Tokimon each : tokimonList) {
            if (each.matchID(feedbackToToki.getID()))
                return true;
        }
        return false;
    }

    private static void printErrorMgs(String errorMgs) {
        System.out.println(errorMgs);
        exit(0);
    }



}
