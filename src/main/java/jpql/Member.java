package jpql;

import javax.persistence.*;

@Entity
// NamedQuery 는 미리 선언해두고 재사용 할 수있다.
// entity 명. 으로 name 을 쓰는것이 관례
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username=:username"
)
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;
    private int age;

    @ManyToOne
    @JoinColumn(name="TEAM_ID")
    private Team team;

    @Enumerated(EnumType.STRING)//항상 enum 타입 쓰려면 문자로 바꿔야함. default 값은 숫자임
    private MemberType type;

    public void changeTeam(Team team){
        this.team=team;
        team.getMembers().add(this);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public MemberType getType() {
        return type;
    }

    public void setType(MemberType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
