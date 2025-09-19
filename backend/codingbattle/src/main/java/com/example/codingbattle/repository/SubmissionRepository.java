package com.example.codingbattle.repository;

import com.example.codingbattle.model.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends MongoRepository<Submission, String> {

    List<Submission> findByRoomId(String roomId);

    List<Submission> findByRoomIdAndParticipantName(String roomId, String participantName);

    List<Submission> findByParticipantName(String participantName);

    @Query("{ 'roomId': ?0, 'isAccepted': true }")
    List<Submission> findAcceptedSubmissionsByRoomId(String roomId);

    @Query("{ 'roomId': ?0, 'participantName': ?1, 'isAccepted': true }")
    Optional<Submission> findAcceptedSubmissionByRoomIdAndParticipant(String roomId, String participantName);

    @Query("{ 'roomId': ?0 }")
    List<Submission> findByRoomIdOrderByCreatedAtDesc(String roomId, Sort sort);

    List<Submission> findTop10ByRoomIdOrderByCreatedAtDesc(String roomId);

    @Query("{ 'createdAt': { '$gte': ?0 } }")
    List<Submission> findRecentSubmissions(LocalDateTime since);

    long countByRoomId(String roomId);

    long countByRoomIdAndIsAccepted(String roomId, boolean isAccepted);

    @Query("{ 'roomId': ?0, 'participantName': ?1 }")
    List<Submission> findByRoomIdAndParticipantNameOrderByCreatedAtDesc(String roomId, String participantName);
}
