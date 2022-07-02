import jpql.Member;
import jpql.MemberDTO;
import jpql.MemberType;
import jpql.Team;

import javax.persistence.*;
import java.util.List;

public class jpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team teamA=new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB=new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1=new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2=new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3=new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);




//            Member member =new Member();
//            member.setUsername("member1");
//            member.setAge(10);
//            member.changeTeam(team); //양방향 값을 넣어주는 함수를 만들어 사용하는것이 좋음
//            member.setType(MemberType.ADMIN);//enum 타입 값 넣기
//            em.persist(member);

            //벌크연산
            //반환값은 멤버(객체)수
            int resultCount = em.createQuery("update Member m set m.age=20")
                    .executeUpdate();

            //벌크 연산시 영속성 컨텍스트에는 age 가 20으로 반영이 안돼있다. 그래서 벌크연산 후 영속성 컨텍스트를 초기화 해줘야한다.
            //초기화 방법은 em.clear 로 영속성 컨텍스트를 비워주고 em.find 를 하면 영속성 컨텍스트는 비어있으니깐 db 에서 값 가져온다.
            em.clear();
            Member findMember = em.find(Member.class, member1.getId());

            //NamedQuery 사용
            List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원1")
                    .getResultList();


            //N+1 문제를 해결하기 위해 연관된 엔티티까지 함께 SQL 한번에 뽑는 페치 조인
            // => 실제 데이터를 한번에 뽑아 저정하므로 반복문시 select 사용 안해도 됨
            //패치 조인을 사용하지 않으면 그때 그때 쿼리가 날라가는(반복문) 지연 로딩에 의해 많은 양의 쿼리가 날라가므로 성능 이슈가 발생함

            //엔티티 페치 조인
            List<Member> result = em.createQuery("select m From Member m join fetch m.team", Member.class)
                    .getResultList();

            //컬렉션 페치 조인
            //컬렉션 페치 조인은 팀A 입장에선 하난데 데이터는 여러개가(중복으로) 뽑히는(데이터 뻥튀기) 문제가 발생함.
            ///이 문제를 해결하려면 DISTINCT 를 사용하면 됨.
            List<Team> result1 = em.createQuery("select distinct t From Team t join t.members", Team.class)
                    .getResultList();

            for (Member member : result) {
                System.out.println("member = " + member.getTeam().getName());
                //회원1은 팀A를 SQL 에서 가져옴
                //회원2는 팀A를 1차캐시에서 가져옴
                //회원3는 팀B를 SQL 에서 가져옴
            }

            /***
            //from 절에서 명시적 조인을 하면 별칭을 얻을수 있다. 이를 이용해 컬렉션 값 연관 경로에서 탐색이 가능하게 한다.
            String query="select m.username From Team t join t.members m";
            TypedQuery<Member> query1 = em.createQuery(query, Member.class);


            //ENUM 을 표현하려면 패키지명부터 다 써야함. 하지만 파라미터 바인딩을 하면 짧게 표현 가능.
            String query= "select m.username, 'Hello', true From Member m "+
                          "where m.type=:userType";
            List<Object[]> result = em.createQuery(query)
                    .setParameter("userType",MemberType.ADMIN)
                    .getResultList();

            for (Object[] objects : result) {
                System.out.println("objects = " + objects[0]);
                System.out.println("objects = " + objects[1]);
                System.out.println("objects = " + objects[2]);
            }



            //내부 조인, inner 대신 외부조인 (left outer join) 사용 가능
            //조인된 테이블이기 때문에 Select m 대신에 t도 사용 가능
            List<Member> resultList1 = em.createQuery("select m from Member m inner join m.team t", Member.class)
                    .getResultList();

            //세타 조인(막 조인)
            List<Member> resultList2 = em.createQuery("select m from Member m, Team t where m.username=t.name", Member.class)
                    .getResultList();

            //페이징 예시
            String jpql= "select m from Member m order by m.username desc";
            List<Member> resultList = em.createQuery(jpql, Member.class)
                    .setFirstResult(10)//10번째 데이터부터
                    .setMaxResults(20)//20개를 가져온다.
                    .getResultList();


            //new 명령어로 조회(단점은 패키지가 길어지면 아래처럼 다 적어야하는 한계가 있음)
            List<MemberDTO> resultList = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();
            MemberDTO memberDTO = resultList.get(0);
            System.out.println("memberDTO = " + memberDTO.getUsername());
            System.out.println("memberDTO = " + memberDTO.getAge());


            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);//Member.class 는 반환할 타입임
            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
            Query query3 = em.createQuery("select m.username, m.age from Member m");//반환 타입이 명확하지 않을때 Query 사용.

            List<Member> resultList = query1.getResultList();//받아올수 있는 값이 여러개일때 리스트로 받고, 없어도 빈 리스트 반환

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }//리스트로 받은 결과 출력

            //파라미터 바인딩-이름 기준(위치 기준으론 사용하지 말자)
            //보통은 이렇게 말고 체이닝으로( .)으로 다엮어서 표현
            query1 = em.createQuery("select m from Member m where m.username=:username", Member.class);
            query1.setParameter("username", "member1");
            query1.getResultList();

            System.out.println("resultList = " + resultList.get(0).getUsername());
             ***/



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();

    }
}

