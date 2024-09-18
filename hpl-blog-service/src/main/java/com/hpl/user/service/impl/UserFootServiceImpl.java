package com.hpl.user.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.pojo.dto.SimpleArticleDTO;
import com.hpl.article.pojo.dto1.SimpleUserInfoDTO;
import com.hpl.count.pojo.enums.CollectionStateEnum;
import com.hpl.count.pojo.enums.CommentStateEnum;
import com.hpl.count.pojo.enums.PraiseStateEnum;
import com.hpl.redis.RedisClient;
import com.hpl.count.pojo.enums.DocumentTypeEnum;
import com.hpl.count.pojo.dto.ArticleCountInfoDTO;
import com.hpl.count.pojo.dto.StatisticUserFootDTO;
import com.hpl.user.context.ReqInfoContext;
import com.hpl.user.pojo.entity.UserFoot;
import com.hpl.user.mapper.UserFootMapper;
import com.hpl.user.service.UserFootService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
@Service
@Slf4j
public class UserFootServiceImpl extends ServiceImpl<UserFootMapper, UserFoot> implements UserFootService {

    @Resource
    private UserFootMapper userFootMapper;

    @Resource
    private RedisClient redisClient;
    
    private final String USER_FOOT_LOCK = "lock:user-foot";

    /**
     * 点赞或取消点赞文章
     * @param articleId
     */
    @Override
    public void praiseArticle(Long articleId){
        // 获取登录用户
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        String key = "praised:user-" + userId;
        // 先用户是否点过赞
        boolean praised = redisClient.sIsMember(key, articleId);

        if (praised) {
            // 如果点过，则取消点赞并减少文章的点赞树
            redisClient.sRem(key, articleId);
            redisClient.incrByStep("statistics:praised:docId-" + articleId, -1L);
        }else {
            // 如果没有点过，则点赞并增加文章的点赞数
            redisClient.sAdd(key, articleId);
            redisClient.incr("statistics:praised:docId-" + articleId);
        }

        //todo 发送给消息队列，让·定时器处理消息，保证redis和数据库的最终一致性

        // 标记
        if(!redisClient.sIsMember("lock:statistics", articleId.toString())) {
            redisClient.sAdd("lock:statistics", articleId.toString());
        }
        lockUserFoot(userId + "-" + articleId);
    }

    /**
     * 收藏或取消收藏文章
     * @param articleId
     */
    @Override
    public void collectArticle(Long articleId){

        // 先获取登录用户
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        String key = "collected:user-" + userId;

        boolean collected = redisClient.sIsMember(key, articleId);
        if (collected) {
            // 如果已经收藏，则取消收藏
            redisClient.sRem(key, articleId);
            redisClient.incrByStep("statistics:collected:docId-" + articleId, -1L);
        }else {
            // 如果没有收藏，则收藏
            redisClient.sAdd(key, articleId);
            redisClient.incr("statistics:collected:docId-" + articleId);
        }

        // 标记
        if(!redisClient.sIsMember("lock:statistics", articleId.toString())) {
            redisClient.sAdd("lock:statistics", articleId.toString());
        }
        lockUserFoot(userId + "-" + articleId);

    }

    /**
     * 如果已经存在，则直接返回，否则加锁，消息队列从该缓存找更新，保证数据库和redis的最终一致性
     * @param value
     */
    private void lockUserFoot(String value) {
        boolean locked = redisClient.sIsMember(USER_FOOT_LOCK, value);
        if(!locked){
            redisClient.sAdd(USER_FOOT_LOCK, value);
        }
    }

    /**
     * 查询用户记录，用于判断是否点过赞、是否评论、是否收藏过
     *
     * @param documentId
     * @param type
     * @param userId
     * @return
     */
    @Override
    public UserFoot getByDocIdAndUserId(Long documentId, Integer type, Long userId){
        LambdaQueryWrapper<UserFoot> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserFoot::getDocumentId, documentId)
                .eq(UserFoot::getDocumentType, type)
                .eq(UserFoot::getUserId, userId);

        return userFootMapper.selectOne(wrapper);
    }

    /**
     * 保存或更新状态信息
     *
     * @param documentType    文档类型：博文 + 评论
     * @param documentId      文档id
     * @param authorId        作者
     * @param userId          操作人
     * @param operateTypeEnum 操作类型：点赞，评论，收藏等
     */
//    @Override
//    public UserFoot saveOrUpdateUserFoot(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum) {
//        // 查询是否有该足迹；有则更新，没有则插入
//        UserFoot readUserFoot = this.getByDocIdAndUserId(documentId, documentType.getCode(), userId);
//        if (readUserFoot == null) {
//            readUserFoot = new UserFoot();
//            readUserFoot.setUserId(userId);
//            readUserFoot.setDocumentId(documentId);
//            readUserFoot.setDocumentType(documentType.getCode());
//            readUserFoot.setDocumentUserId(authorId);
//            setUserFootState(readUserFoot, operateTypeEnum);
//            userFootMapper.insert(readUserFoot);
//        } else if (setUserFootState(readUserFoot, operateTypeEnum)) {
////            readUserFoot.setUpdateTime(new Date());
//            userFootMapper.updateById(readUserFoot);
//        }
//        return readUserFoot;
//    }

//    private boolean setUserFootState(UserFoot userFoot, OperateTypeEnum operate) {
//        switch (operate) {
//            case READ:
//                // 设置为已读
//                userFoot.setReadState(1);
//                // 需要更新时间，用于浏览记录
//                return true;
//            case PRAISE:
//            case CANCEL_PRAISE:
//                return compareAndUpdate(userFoot::getPraiseState, userFoot::setPraiseState, operate.getDbStatCode());
//            case COLLECTION:
//            case CANCEL_COLLECTION:
//                return compareAndUpdate(userFoot::getCollectionState, userFoot::setCollectionState, operate.getDbStatCode());
//            case COMMENT:
//            case DELETE_COMMENT:
//                return compareAndUpdate(userFoot::getCommentState, userFoot::setCommentState, operate.getDbStatCode());
//            default:
//                return false;
//        }
//    }


    /**
     * 比较并更新状态。
     * 使用Supplier获取当前状态，与输入参数进行比较。如果两者不相等，则更新状态为输入参数，并返回true；
     * 如果两者相等，则不更新状态，直接返回false。这种方法常用于判断并更新某种状态或属性，
     * 以避免不必要的更新操作，提高效率。
     *
     * @param supplier 用于获取当前状态的函数接口，不接受任何参数，返回当前状态。
     * @param consumer 用于更新状态的函数接口，接受一个新的状态作为参数，无返回值。
     * @param input 新的状态或值，用于与当前状态比较，或更新当前状态。
     * @param <T> 状态或值的类型。
     * @return 如果状态被更新，则返回true；如果状态未被更新，即输入与当前状态相等，则返回false。
     */
    private <T> boolean compareAndUpdate(Supplier<T> supplier, Consumer<T> consumer, T input) {
        // 比较当前状态和输入参数是否相等
        if (Objects.equals(supplier.get(), input)) {
            return false;
        }
        // 如果不相等，则更新状态为输入参数
        consumer.accept(input);
        return true;
    }


//    @Override
//    public void saveCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor) {
//        // 保存文章对应的评论足迹
//        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
//        // 如果是子评论，则找到父评论的记录，然后设置为已评
//        if (comment.getParentCommentId() != null && comment.getParentCommentId() != 0) {
//            // 如果需要展示父评论的子评论数量，authorId 需要传父评论的 userId
//            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
//        }
//    }


//    @Override
//    public void removeCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor) {
//        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
//        if (comment.getParentCommentId() != null) {
//            // 如果需要展示父评论的子评论数量，authorId 需要传父评论的 userId
//            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
//        }
//    }



//    /**
//     * 根据用户ID和分页参数，查询用户已读文章的ID列表。
//     *
//     * @param userId 用户ID，用于筛选用户足迹记录。
//     * @param pageParam 分页参数，包含页码和每页数量，用于限制返回的结果数量。
//     * @return 返回一个Long类型的列表，包含用户已读文章的ID。
//     *
//     * 此方法通过查询用户足迹表（UserFoot），筛选出特定用户已读的文章记录，
//     * 并按照更新时间降序排列，最后根据分页参数限制返回的结果数量。
//     */
//    @Override
//    public List<Long> listReadedAIdsByUId(Long userId, CommonPageParam pageParam) {
//        // 创建查询条件包装对象
//        LambdaQueryWrapper<UserFoot> wrapper=new LambdaQueryWrapper<>();
//
//        // 设置查询条件：选择字段为文档ID，筛选条件为用户ID、文档类型为文章、阅读状态为已读
//        // 并按照更新时间降序排列，最后根据分页参数拼接LIMIT语句
//        wrapper.select(UserFoot::getDocumentId)
//                .eq(UserFoot::getUserId, userId)
//                .eq(UserFoot::getDocumentType, DocumentTypeEnum.ARTICLE.getCode())
//                .eq(UserFoot::getReadState, 1)
//                .orderByDesc(UserFoot::getUpdateTime)
//                .last(CommonPageParam.getLimitSql(pageParam));
//
//        // 执行查询，将结果映射为文章ID列表并返回
//        return userFootMapper.selectList(wrapper).stream()
//                .map(UserFoot::getDocumentId)
//                .collect(Collectors.toList());
//    }



//    /**
//     * 查询用户收藏的文章列表。
//     *
//     * 通过用户ID和分页参数，查询用户收藏的文章ID列表。
//     * 查询条件包括：用户ID、文档类型为文章、收藏状态为已收藏。
//     * 查询结果按照更新时间降序排序，并根据分页参数限制返回结果的数量。
//     *
//     * @param userId 用户ID，用于指定查询哪个用户的收藏文章。
//     * @param pageParam 分页参数，包含当前页码和每页的条数，用于分页查询。
//     * @return 返回一个文章ID的列表，表示该用户收藏的文章。
//     */
//    @Override
//    public List<Long> listCollectionedAIdsByUId(Long userId, CommonPageParam pageParam) {
//        // 创建查询条件包装对象，用于构建查询语句
//        LambdaQueryWrapper<UserFoot> wrapper=new LambdaQueryWrapper<>();
//        // 设置查询条件：选择字段为文档ID，用户ID等于传入的userId，文档类型为文章，收藏状态为已收藏
//        wrapper.select(UserFoot::getDocumentId)
//                .eq(UserFoot::getUserId, userId)
//                .eq(UserFoot::getDocumentType, DocumentTypeEnum.ARTICLE.getCode())
//                .eq(UserFoot::getCollectionState, CollectionStateEnum.COLLECTION.getCode())
//                // 按更新时间降序排序
//                .orderByDesc(UserFoot::getUpdateTime)
//                // 根据分页参数设置查询的限制条件
//                .last(CommonPageParam.getLimitSql(pageParam));
//
//        // 执行查询，将结果转换为文章ID的列表返回
//        return userFootMapper.selectList(wrapper).stream()
//                .map(UserFoot::getDocumentId)
//                .collect(Collectors.toList());
//    }


    @Override
    public List<SimpleUserInfoDTO> getArticlePraisedUsers(Long articleId) {

        return userFootMapper.listSimpleUserInfosByArticleId(articleId, DocumentTypeEnum.ARTICLE.getCode(), 10);

    }

    @Override
    public StatisticUserFootDTO getFootCount() {
        return userFootMapper.getFootCount();
    }


    /**
     * 查询文章计数信息
     *
     * @param articleId
     * @return
     */
    @Override
    public ArticleCountInfoDTO countArticleByArticleId(Long articleId){
        return userFootMapper.countArticleByArticleId(articleId);
    }

    @Override
    public ArticleCountInfoDTO countArticleByUserId(Long userId){
        return userFootMapper.countArticleByUserId(userId);
    }

    @Override
    public void handleUpdateUserFoot(){
        // 先查询user_foot锁信息
        Set<String> locks = redisClient.sMembers("lock:user-foot");
        log.warn(locks.toString());
        log.warn("locks' length：{}",locks.size());

        locks.forEach(t->{
             String[] str= t.split("-");
            Long userId = Long.parseLong(str[0]);
            String documentId = str[1];

            // 查询数据库是否存在记录
            LambdaQueryWrapper<UserFoot> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserFoot::getUserId, userId)
                    .eq(UserFoot::getDocumentId, Long.parseLong(documentId));
            UserFoot userFoot = this.getOne(wrapper);

            if(userFoot == null){ // 新增
                userFoot = loadFromRedis(userId,documentId);
                this.save(userFoot);
            }else{ // 更新
                UserFoot cache = loadFromRedis(userId,documentId);
                userFoot.setPraised(cache.getPraised());
                userFoot.setCollected(cache.getCollected());
                userFoot.setCommented(cache.getCommented());
                this.save(userFoot);
            }

            // 删除lock标记
            redisClient.sRem("lock:user-foot",t);
        });

    }


    /**
     * 从redis中加载点赞、收藏、评论信息
     * @param userId
     * @param documentId
     * @return
     */
    private UserFoot loadFromRedis(Long userId,String documentId){
        redisClient.sIsMember("praised:user-"+userId,documentId);
        UserFoot userFoot = new UserFoot();
        userFoot.setUserId(userId);
        userFoot.setDocumentId(Long.parseLong(documentId));
        userFoot.setDocumentType(DocumentTypeEnum.ARTICLE.getCode());

        userFoot.setPraised(redisClient.sIsMember("praised:user-"+userId,documentId)
                ? PraiseStateEnum.PRAISED.getCode()
                :PraiseStateEnum.NOT_PRAISED.getCode());

        userFoot.setCollected(redisClient.sIsMember("collected:user-"+userId,documentId)
                ? CollectionStateEnum.COLLECTED.getCode()
                :CollectionStateEnum.NOT_CONTAINED.getCode());

        userFoot.setCommented(redisClient.sIsMember("commented:user-"+userId,documentId)
                ? CommentStateEnum.COMMENTED.getCode()
                :CommentStateEnum.NOT_COMMENTED.getCode());

        return userFoot;
    }

//    @Override
//    public Long countCommentPraise(Long commentId){
//        return lambdaQuery()
//                .eq(UserFoot::getDocumentId, commentId)
//                .eq(UserFoot::getDocumentType, DocumentTypeEnum.COMMENT.getCode())
//                .eq(UserFoot::getPraiseState, PraiseStateEnum.PRAISE.getCode())
//                .count();
//    }

    /**
     * 获取登录用户最近阅读文章
     * @return
     */
    @Override
    public List<SimpleArticleDTO> getReadRecent(){
        // 获取登录用户id
        Long userId = ReqInfoContext.getReqInfo().getUserId();
        List<String> res = redisClient.lRange("recent:"+userId,0,-1);

        return res.stream()
                .map(t-> JSONUtil.toBean(t,SimpleArticleDTO.class))
                .collect(Collectors.toList());
    }
}
