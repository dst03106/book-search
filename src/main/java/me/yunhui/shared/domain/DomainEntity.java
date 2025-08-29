package me.yunhui.shared.domain;

/**
 * 도메인 엔티티의 기본 인터페이스
 * 고유 식별자를 가지는 도메인 객체를 나타냅니다.
 */
public interface DomainEntity<ID> {
    
    /**
     * 엔티티의 고유 식별자를 반환합니다.
     * 
     * @return 엔티티의 고유 식별자
     */
    ID getId();
    
    /**
     * 두 엔티티가 같은 식별자를 가지는지 확인합니다.
     * 
     * @param other 비교할 엔티티
     * @return 같은 식별자를 가지면 true, 그렇지 않으면 false
     */
    default boolean sameIdentityAs(DomainEntity<ID> other) {
        if (other == null || getId() == null) {
            return false;
        }
        return getId().equals(other.getId());
    }
}