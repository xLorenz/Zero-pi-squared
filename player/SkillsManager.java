package player;

import java.util.List;
import java.util.ArrayList;

import player.skills.Skill;

public class SkillsManager {

    private ArrayList<Skill> skills = new ArrayList<>();

    public Skill selectedSkill;

    private Player player;

    public SkillsManager(Player p) {
        this.player = p;
    }

    public void updateTimers(double dt) {
        for (Skill s : skills) {
            s.updateTimer(dt);
        }
    }

    public void updateSkills(double dt) {
        for (Skill s : skills) {
            s.update(dt, player);
        }
    }

    public void handleInputs(Controller c) {
        for (Skill s : skills) {
            s.handleInputs(c);
        }
    }

    public void addSkill(Skill s) {
        skills.add(s);
    }

    public void addSkills(List<Skill> skills) {
        for (Skill s : skills)
            this.skills.add(s);
    }

    public void removeSkill(Skill s) {
        if (s != null)
            if (skills.contains(s))
                skills.remove(s);

    }

}
