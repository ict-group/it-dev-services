package dev.it.api.service.v4;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

public interface RsServiceV4Search<T> {
    String getDefaultOrderBy();

    PanacheQuery<T> getSearch(String orderBy) throws Exception;

    default Sort sort(String orderBy) throws Exception {
        Sort sort = null;
        if (orderBy != null && !orderBy.trim().isEmpty()) {
            if (orderBy != null && orderBy.contains(",")) {
                String[] orderByClause = orderBy.split(",");
                for (String pz : orderByClause) {
                    sort = single(sort, pz);
                }
                return sort;
            } else {
                return single(sort, orderBy);
            }
        }
        if (getDefaultOrderBy() != null && !getDefaultOrderBy().trim().isEmpty()) {
            if (getDefaultOrderBy().contains("asc"))
                return Sort.by(getDefaultOrderBy().replace("asc", "").trim()).ascending();
            if (getDefaultOrderBy().contains("desc"))
                return Sort.by(getDefaultOrderBy().replace("desc", "").trim()).ascending();
        }

        return null;
    }

    default Sort single(Sort sort, String orderBy) throws Exception {
        String[] orderByClause;
        if (orderBy.contains(":")) {
            orderByClause = orderBy.split(":");
        } else {
            orderByClause = orderBy.split(" ");
        }
        if (orderByClause.length > 1) {
            if (orderByClause[1].equals("asc")) {
                if (sort != null) {
                    return sort.and(orderByClause[0], Sort.Direction.Ascending);
                } else {
                    return Sort.by(orderByClause[0], Sort.Direction.Ascending);
                }

            } else if (orderByClause[1].equals("desc")) {
                if (sort != null) {
                    return sort.and(orderByClause[0], Sort.Direction.Descending);
                } else {
                    return Sort.by(orderByClause[0], Sort.Direction.Descending);
                }
            }
            throw new Exception("sort is not usable");
        } else {
            if (sort != null) {
                return sort.and(orderBy).descending();
            } else {
                return Sort.by(orderBy).ascending();
            }
        }
    }

}
