# Git Quick Reference

## One-time setup
    git config --global user.name "Your Name"
    git config --global user.email "your@email.com"

## Clone
    git clone https://github.com/L-Tsats/ml-destination-dispatch.git
    cd ml-destination-dispatch

## Daily loop
    git pull                        # always first
    # ... do your work ...
    git status                      # see what changed
    git add filename                # stage a file
    git add .                       # stage everything
    git commit -m "what you did"    # save snapshot
    git push                        # share with team

## Useful checks
    git log --oneline               # see history
    git diff                        # see unstaged changes
    git restore filename            # undo unsaved change

## Rejected push (someone else pushed first)
    git pull --rebase
    git push

## THE CACHE BUG (if push says "repository not found")
Git is silently using a cached login for the wrong account.
Fix:
    cmdkey /delete:LegacyGeneric:target=git:https://github.com
Then push again and sign in fresh when prompted.
If that entry doesn't exist, try:
    cmdkey /list | findstr github
and delete whatever shows up with:
    cmdkey /delete:<paste the target name here>
Then push and sign into the RIGHT account (the one that has access).
