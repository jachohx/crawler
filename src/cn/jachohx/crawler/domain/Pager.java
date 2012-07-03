package cn.jachohx.crawler.domain;

import java.util.List;

public class Pager <T> {
    private int pageNo;
    private int pageSize;
    private int total;

    private List <T> resultList;

    public int getPageCount() {
        return total / pageSize + (total % pageSize == 0 ? 0 : 1);
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List <T> getResultList() {
        return resultList;
    }

    public int getTotal() {
        return total;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setResultList(List <T> resultList) {
        this.resultList = resultList;
    }

}
